package com.windimg.screenshotdemo

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import java.io.*
import java.math.BigDecimal


object FileUtil {

    // 获取指定文件夹内所有文件大小的和
    fun getFolderSize(file: File): Long {
        var size: Long = 0
        try {
            val fileList = file.listFiles()
            for (aFileList in fileList) {
                size = if (aFileList.isDirectory) {
                    size + getFolderSize(aFileList)
                } else {
                    size + aFileList.length()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return size
    }

    // 按目录删除文件夹文件方法
    fun deleteFolderFile(filePath: String, deleteThisPath: Boolean): Boolean {
        try {
            val file = File(filePath)
            if (file.isDirectory) {
                val files = file.listFiles()
                for (file1 in files) {
                    deleteFolderFile(file1.absolutePath, true)
                }
            }
            if (deleteThisPath) {
                if (!file.isDirectory) {
                    file.delete()
                } else {
                    if (file.listFiles().isEmpty()) {
                        file.delete()
                    }
                }
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    /**
     * 将文件转为byte[]
     * @param filePath 文件路径
     * @return
     */
    fun getBytes(filePath: String?): ByteArray? {
        val file = File(filePath)
        val out = ByteArrayOutputStream()
        try {
            val ins = FileInputStream(file)
            val b = ByteArray(1024)
            var i: Int
            do {
                i = ins.read(b)
                if (i == -1) {
                    break
                }
                out.write(b, 0, b.size)
            } while (true)
            out.close()
            ins.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return out.toByteArray()
    }

    // 格式化单位
    fun getFormatSize(size: Double): String {
        val kiloByte = size / 1024
        if (kiloByte == 0.0) {
            return "0 KB"
        }
        val megaByte = kiloByte / 1024
        if (megaByte < 1) {
            val result1 = BigDecimal(kiloByte.toString())
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " KB"
        }
        val gigaByte = megaByte / 1024
        if (gigaByte < 1) {
            val result2 = BigDecimal(megaByte.toString())
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " MB"
        }
        val teraBytes = gigaByte / 1024
        if (teraBytes < 1) {
            val result3 = BigDecimal(gigaByte.toString())
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " GB"
        }
        val result4 = BigDecimal(teraBytes)
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + " TB"
    }

    fun fileCopy(oldFilePath: String, newFilePath: String): Boolean {
        //如果原文件不存在
        if (!fileExists(oldFilePath)) {
            return false
        }
        //获得原文件流
        val inputStream = FileInputStream(File(oldFilePath))
        val data = ByteArray(1024)
        //输出流
        val outputStream = FileOutputStream(File(newFilePath))
        //开始处理流
        while (inputStream.read(data) != -1) {
            outputStream.write(data)
        }
        inputStream.close()
        outputStream.close()
        return true
    }

    fun fileExists(filePath: String): Boolean {
        val file = File(filePath)
        return file.exists()
    }

    fun createDirNoExist(watchCoreDir: String) {
        val f = File(watchCoreDir)
        if (!f.exists()) {
            if (!f.mkdirs()) {
                Log.e(TAG, "Failed to create business directory: $watchCoreDir")
            }
        }
    }

    fun copyAssetsToSdcard(context: Context, src: String, destPath: String) {
        val assetManager = context.assets
        var `in`: InputStream? = null
        var out: OutputStream? = null
        try {
            `in` = assetManager.open(src)
            val outFile = File(destPath, src)
            out = FileOutputStream(outFile)
            copyFile(`in`!!, out)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (`in` != null) {
                try {
                    `in`.close()
                } catch (e: IOException) {
                    // NOOP
                }
                if (out != null) {
                    try {
                        out.close()
                    } catch (e: IOException) {
                        // NOOP
                    }

                }
            }
        }
    }

    @Throws(IOException::class)
    fun copyFile(`in`: InputStream, out: OutputStream) {
        val buffer = ByteArray(1024)
        var read: Int = `in`.read(buffer)
        while (read != -1) {

            out.write(buffer, 0, read)
            read = `in`.read(buffer)
        }
    }

    /**
     * 保存位图到本地
     *
     * @param bitmap
     * @param path   本地路径
     * @return boolean
     */
    fun saveImage(bitmap: Bitmap, path: String): String? {
        val file = File(path)
        val fileOutputStream: FileOutputStream
        //文件夹不存在，则创建它
        if (!file.exists()) {
            file.mkdir()
        }
        try {
            val imgName = path + File.separator + System.currentTimeMillis() + ".jpg"
            fileOutputStream = FileOutputStream(imgName)
            val isSuccess = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()

            // 广播回到UI线程再发送
            return if (isSuccess) {
                imgName
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }
}