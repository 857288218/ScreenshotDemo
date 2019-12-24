package com.windimg.screenshotdemo

import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore

/**
 * 媒体内容观察者(观察媒体数据库的改变)
 */
class MediaContentObserver (private val mContentUri: Uri, handler: Handler, val handleMediaRowData: (String, Long)->Unit) : ContentObserver(handler) {

    /** 读取媒体数据库时需要读取的列  */
    private val MEDIA_PROJECTIONS = arrayOf(MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DATE_TAKEN)

    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        handleMediaContentChange(mContentUri)
    }

    private fun handleMediaContentChange(contentUri: Uri) {
        var cursor: Cursor? = null
        try {
            // 数据改变时查询数据库中最后加入的一条数据
            cursor = App.context.contentResolver.query(
                contentUri,
                MEDIA_PROJECTIONS, null, null,
                MediaStore.Images.ImageColumns.DATE_ADDED + " desc limit 1"
            )

            if (cursor == null || !cursor.moveToFirst()) {
                return
            }

            // 获取各列的索引
            val dataIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            val dateTakenIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN)

            // 获取行数据
            val data = cursor.getString(dataIndex)
            val dateTaken = cursor.getLong(dateTakenIndex)

            // 处理获取到的第一行数据
            Handler(Looper.getMainLooper()).post { handleMediaRowData(data, dateTaken) }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
        }
    }
}