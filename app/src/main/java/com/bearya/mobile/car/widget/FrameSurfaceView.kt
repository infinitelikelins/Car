package com.bearya.mobile.car.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

/**
 * 使用SurfaceView去加载帧动画解决OOM的问题
 */
class FrameSurfaceView : SurfaceView, SurfaceHolder.Callback {

    private var mSurfaceHolder: SurfaceHolder? = null

    private var mIsThreadRunning = true // 线程运行开关

    private var mIsDestroy: Boolean = false

    // 用于播放动画的图片资源id数组
    var mBitmapResourceIds: IntArray? = null
        set(value) {
            field = value
            totalCount = field?.size ?: 0
        }

    private var totalCount: Int = 0//资源总数
    private var mCanvas: Canvas? = null
    private var mBitmap: Bitmap? = null// 显示的图片

    private var mCurrentIndex: Int = 0// 当前动画播放的位置
    var mGapTime: Int? = 20// 每帧动画持续存在的时间
    var mIsRepeat = false

    var mOnFrameFinishedListener: OnFrameFinishedListener? = null// 动画监听事件
    private var animationThread: Thread? = null

    constructor(context: Context) : super(context, null, 0) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs, 0) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private fun initView() {

        mSurfaceHolder = this.holder
        mSurfaceHolder?.addCallback(this)

        // 白色背景
        setZOrderOnTop(true)
        setZOrderMediaOverlay(true)
    }

    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
        mIsDestroy = false
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
        mIsDestroy = true
    }

    /**
     * 制图方法
     */
    private fun drawView() {
        // 无资源文件退出
        if (mBitmapResourceIds == null) {
            mIsThreadRunning = false
            return
        }

        // 锁定画布
        mCanvas = mSurfaceHolder?.lockCanvas()

        try {
            if (mSurfaceHolder != null && mCanvas != null) {

                mCanvas?.drawColor(Color.WHITE)

                if (mBitmapResourceIds?.isNotEmpty() == true)
                    mBitmap = BitmapFactory.decodeResource(resources, mBitmapResourceIds!![mCurrentIndex])
                val paint = Paint()
                paint.isAntiAlias = true
                paint.style = Paint.Style.STROKE
                val mSrcRect = Rect(0, 0, mBitmap!!.width, mBitmap!!.height)
                val mDestRect = Rect(0, 0, width, height)

                mCanvas?.drawBitmap(mBitmap!!, mSrcRect, mDestRect, paint)

                // 播放到最后一张图片
                if (mCurrentIndex == totalCount - 1) {
                    //播放到最后一张，当前index置零
                    if (mIsRepeat) {
                        mCurrentIndex = 0
                    } else {
                        stop()
                    }
                }
            }
        } catch (e: Exception) {

        } finally {
            mCurrentIndex++
            if (mCurrentIndex >= totalCount) {
                mCurrentIndex = 0
            }
            if (mCanvas != null) {
                // 将画布解锁并显示在屏幕上
                mSurfaceHolder?.unlockCanvasAndPost(mCanvas)
            }
            // 收回图片
            mBitmap?.recycle()
        }
    }

    /**
     * 开始动画
     */
    fun start() {
        if (!mIsDestroy) {
            mCurrentIndex = 0
            mIsThreadRunning = true
            if (animationThread?.isInterrupted?.not() == true) {
                animationThread?.interrupt()
            }
            animationThread = null
            animationThread = Thread {
                mOnFrameFinishedListener?.onFrameStart()
                // 每隔150ms刷新屏幕
                while (mIsThreadRunning) {
                    try {
                        drawView()
                        Thread.sleep(mGapTime?.toLong() ?: 20L)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                mOnFrameFinishedListener?.onFrameFinish()
            }
            animationThread?.start()
        } else {
            // 如果SurfaceHolder已经销毁抛出该异常
            try {
                throw Exception("IllegalArgumentException:Are you sure the SurfaceHolder is not destroyed")
            } catch (e: Exception) {

            }
        }
    }

    /**
     * 结束动画
     */
    fun stop() {
        mIsThreadRunning = false
        if (animationThread?.isInterrupted?.not() == true) {
            animationThread?.interrupt()
        }
        if (animationThread?.isAlive == true) {
            animationThread?.interrupt()
        }
    }

    /**
     * 继续动画
     */
    fun resume() {
        mIsThreadRunning = false
    }

    /**
     * 动画监听器
     *
     * @author qike
     */
    interface OnFrameFinishedListener {

        /**
         * 动画开始
         */
        fun onFrameStart()

        /**
         * 动画结束
         */
        fun onFrameFinish()
    }

    /**
     * 当用户点击返回按钮时，停止线程，反转内存溢出
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        // 当按返回键时，将线程停止，避免surfaceView销毁了,而线程还在运行而报错
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mIsThreadRunning = false
        }
        return super.onKeyDown(keyCode, event)
    }

}