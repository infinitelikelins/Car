package com.bearya.mobile.car.activity

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.startup.AppInitializer
import com.bearya.mobile.car.BuildConfig
import com.bearya.mobile.car.R
import com.bearya.mobile.car.adapter.DeviceAdapter
import com.bearya.mobile.car.databinding.ActivityMainBinding
import com.bearya.mobile.car.model.MainViewModel
import com.bearya.mobile.car.startup.BluetoothInit
import com.kaopiz.kprogresshud.KProgressHUD
import com.kelin.apkUpdater.ApkUpdater
import com.kelin.apkUpdater.util.NetWorkStateUtil
import com.vise.baseble.ViseBle
import com.vise.baseble.callback.IConnectCallback
import com.vise.baseble.callback.scan.IScanCallback
import com.vise.baseble.callback.scan.ScanCallback
import com.vise.baseble.core.DeviceMirror
import com.vise.baseble.exception.BleException
import com.vise.baseble.model.BluetoothLeDevice
import com.vise.baseble.model.BluetoothLeDeviceStore
import com.vmadalin.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private val permissionBluetoothRequestCode = 0x11
    private val permissionFileRequestCode = 0x12

    private lateinit var bindView: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private val deviceAdapter: DeviceAdapter by lazy {
        DeviceAdapter().apply {
            setOnItemClickListener { _, _, position ->
                connectDevice(getItem(position))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindView = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindView.root)

        bindView.carDevices.apply {
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            adapter = deviceAdapter
        }
        inflateEmptyView(getString(R.string.welcome))

        viewModel.newApk.observe(this) {
            if (it != null && it.versionCode > BuildConfig.VERSION_CODE) {
                if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    ApkUpdater.Builder().create().check(it, autoCheck = true, autoInstall = true)
                } else {
                    EasyPermissions.requestPermissions(
                        this,
                        "更新应用需要使用外部存储保存下载内容",
                        permissionFileRequestCode,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                }
            } else {
                Toast.makeText(this, "当前已是最新版本", Toast.LENGTH_SHORT).show()
            }
        }

        appUpdate()

    }

    /**
     * 1. 检查设备是否支持蓝牙
     */
    private fun initBluetoothFeature() {
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            checkBluetoothStatus()
        } else {
            deviceAdapter.removeEmptyView()
            deviceAdapter.setEmptyView(R.layout.disable_devices)
        }
    }

    /**
     * 2. 检查系统蓝牙是否开启
     */
    private fun checkBluetoothStatus() {
        val bluetoothAdapter =
            (getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager?)?.adapter
        if (bluetoothAdapter?.isEnabled == true) {
            checkPermission()
        } else {
            addBluetoothStatusListener(bluetoothAdapter)
        }
    }

    /**
     * 3. 开启系统蓝牙并且监听系统蓝牙的打开关闭状态变化
     */
    private fun addBluetoothStatusListener(bluetoothAdapter: BluetoothAdapter?) {
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    BluetoothAdapter.ACTION_STATE_CHANGED -> {
                        if (bluetoothAdapter?.state == BluetoothAdapter.STATE_ON) {
                            checkPermission()
                        }
                    }
                }
            }
        }, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
        bluetoothAdapter?.enable()
    }

    /**
     * 4. 检查蓝牙所需要位置的权限
     */
    private fun checkPermission() {
        if (EasyPermissions.hasPermissions(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            startScanDevice()
        } else {
            EasyPermissions.requestPermissions(
                this,
                "发现蓝牙设备需要打开并使用近距离定位功能",
                permissionBluetoothRequestCode,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    /**
     * 5.初始化ViseBle框架,开始搜索
     */
    private fun startScanDevice() {
        AppInitializer.getInstance(this).initializeComponent(BluetoothInit::class.java)
        deviceAdapter.removeAllHeaderView()
        deviceAdapter.removeAllFooterView()
        deviceAdapter.removeEmptyView()
        deviceAdapter.setList(null)

        val hud = KProgressHUD.create(this)
            .setLabel("正在搜索中...")
            .setDetailsLabel("请确保机器人开启蓝牙模式")
            .setCancellable(false)
            .show()
        ViseBle.getInstance().startScan(ScanCallback(object : IScanCallback {
            override fun onDeviceFound(bluetoothLeDevice: BluetoothLeDevice?) {

            }

            override fun onScanFinish(bluetoothLeDeviceStore: BluetoothLeDeviceStore?) {
                runOnUiThread {
                    inflateEmptyView(getString(R.string.empty_devices))
                    deviceAdapter.setDiffNewData(bluetoothLeDeviceStore?.deviceList?.filter {
                        it.name != null && it.name.startsWith("贝芽", true)
                    }?.toMutableList().apply {
                        if (!isNullOrEmpty()) {
                            inflateHeaderAndFooterView()
                        }
                    })
                    hud.dismiss()
                }
            }

            override fun onScanTimeout() {
                runOnUiThread {
                    inflateEmptyView(getString(R.string.empty_devices))
                    hud.dismiss()
                }
            }
        }))
    }

    /**
     * 指定点击的设备蓝牙连接进行数据交换
     */
    private fun connectDevice(device: BluetoothLeDevice?) {
        if (device == null) {
            Toast.makeText(this@MainActivity, "连接失败 : 设备不能为空", Toast.LENGTH_LONG).show()
            return
        }
        val hud = KProgressHUD.create(this).setLabel("正在连接中...").setCancellable(false).show()
        ViseBle.getInstance().connect(device, object : IConnectCallback {
            override fun onConnectSuccess(deviceMirror: DeviceMirror?) {
                runOnUiThread {
                    if (deviceMirror != null) {
                        startActivity(
                            Intent(this@MainActivity, EmotionActivity::class.java)
                                .putExtra("device", device)
                        )
                    } else {
                        Toast.makeText(this@MainActivity, "设备错误", Toast.LENGTH_LONG).show()
                    }
                    hud.dismiss()
                }
            }

            override fun onConnectFailure(exception: BleException?) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "连接失败", Toast.LENGTH_LONG).show()
                    hud.dismiss()
                }
            }

            override fun onDisconnect(isActive: Boolean) {
                runOnUiThread {
                    LocalBroadcastManager.getInstance(this@MainActivity)
                        .sendBroadcast(Intent("ACTION_FINISH"))
                    Toast.makeText(this@MainActivity, "蓝牙已断开", Toast.LENGTH_LONG).show()
                    hud.dismiss()
                }
            }
        })
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {

    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        if (requestCode == permissionBluetoothRequestCode) startScanDevice()
        if (requestCode == permissionFileRequestCode) appUpdate()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onDestroy() {
        super.onDestroy()
        ViseBle.getInstance().disconnect()
        ViseBle.getInstance().clear()
    }

    @SuppressLint("SetTextI18n")
    private fun inflateEmptyView(tips: String) {
        val initEmptyView =
            layoutInflater.inflate(R.layout.empty_devices, bindView.root, false).apply {
                findViewById<AppCompatTextView>(R.id.empty_text).text = tips
                findViewById<AppCompatButton>(R.id.search_devices).apply {
                    setOnClickListener {
                        initBluetoothFeature()
                    }
                }
                findViewById<AppCompatButton>(R.id.version_code).apply {
                    text = "当前版本：${BuildConfig.VERSION_NAME}"
                    setOnClickListener {
                        appUpdate()
                    }
                }
            }
        deviceAdapter.setEmptyView(initEmptyView)
    }

    @SuppressLint("SetTextI18n")
    private fun inflateHeaderAndFooterView() {
        deviceAdapter.addHeaderView(
            layoutInflater.inflate(
                R.layout.header_devices,
                bindView.root,
                false
            )
        )
        val footerView =
            layoutInflater.inflate(R.layout.footer_devices, bindView.root, false).apply {
                findViewById<AppCompatButton>(R.id.search_devices).apply {
                    setOnClickListener {
                        initBluetoothFeature()
                    }
                }
                findViewById<AppCompatButton>(R.id.version_code).apply {
                    text = "当前版本：${BuildConfig.VERSION_NAME}"
                    setOnClickListener {
                        appUpdate()
                    }
                }
            }
        deviceAdapter.addFooterView(footerView)
    }

    private fun appUpdate() {
        if (NetWorkStateUtil.isConnected(this)) {
            val hud = KProgressHUD.create(this@MainActivity)
                .setLabel("正在联网检查更新中...").setCancellable(false).show()
            viewModel.checkUpdate {
                hud.dismiss()
            }
        } else {
            Toast.makeText(this@MainActivity, "设备未联网，请联网后再试", Toast.LENGTH_LONG).show()
        }
    }

}