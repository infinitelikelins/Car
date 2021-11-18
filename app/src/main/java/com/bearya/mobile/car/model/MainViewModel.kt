package com.bearya.mobile.car.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bearya.mobile.car.ext.setData
import com.bearya.mobile.car.http.Api
import com.kelin.apkUpdater.UpdateInfoImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val newApk: MutableLiveData<UpdateInfoImpl?> by lazy { MutableLiveData<UpdateInfoImpl?>() }

    fun checkUpdate(finish: (() -> Unit)? = null) = viewModelScope.launch {
        try {
            delay(3000)
            val response = Api.checkAppUpdate()
            val data = response?.data?.toCheckUpdateInfoImpl()
            newApk.setData(data)
        } catch (exception: Exception) {
            newApk.setData(null)
        } finally {
            withContext(Dispatchers.Main) {
                finish?.invoke()
            }
        }
    }

}