package com.windimg.screenshotdemo

import android.Manifest
import android.database.ContentObserver
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log


class MainActivity : AppCompatActivity() {

    /** 内部存储器内容观察者  */
    private lateinit var mInternalObserver: ContentObserver

    /** 外部存储器内容观察者  */
    private lateinit var mExternalObserver: ContentObserver

    private var mHandlerThread: HandlerThread? = null
    private var mHandler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mHandlerThread = HandlerThread("Screenshot_Observer")
        mHandlerThread?.start()
        mHandler = Handler(mHandlerThread?.looper)

        //需要申请读sd卡权限
        PermissionRequest(this).requestPermissions(
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            object : PermissionListener {
                override fun onDenied(deniedPermission: MutableList<String>?) {
                }

                override fun onGranted() {
                }

                override fun onShouldShowRationale(deniedPermission: MutableList<String>?) {
                }
            })

        // 初始化
        mInternalObserver = MediaContentObserver(
            MediaStore.Images.Media.INTERNAL_CONTENT_URI,
            mHandler!!,
            ::handleMediaRowData
        )
        mExternalObserver = MediaContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            mHandler!!,
            ::handleMediaRowData
        )
    }

    override fun onResume() {
        super.onResume()
        // 添加监听
        this.contentResolver.registerContentObserver(
            MediaStore.Images.Media.INTERNAL_CONTENT_URI,
            false,
            mInternalObserver
        )
        this.contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            false,
            mExternalObserver
        )
    }

    override fun onPause() {
        super.onPause()
        // 注销监听
        this.contentResolver.unregisterContentObserver(mInternalObserver)
        this.contentResolver.unregisterContentObserver(mExternalObserver)
    }

    /**
     * 处理监听到的资源
     */
    private fun handleMediaRowData(data: String, dateTaken: Long) {
        if (Utils.checkScreenShot(data, dateTaken) && FileUtil.fileExists(data)) {
            //todo 如果分享弹窗弹出了就不再显示了，有可能会收到多次截屏的结果
            Log.d("rjq", "$data $dateTaken")
        } else {
            Log.d("rjq", "Not screenshot event")
        }
    }
}
