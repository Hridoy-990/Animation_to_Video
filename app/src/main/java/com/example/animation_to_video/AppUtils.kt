package com.example.animation_to_video

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

/**
 * @author Md Jahirul Islam Hridoy
 * Created on 27,March,2022
 */


fun needsPermission(context: Context): Boolean {
    return Build.VERSION.SDK_INT >= 23 && context.checkSelfPermission(
        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
}

fun requestPermission(activity: AppCompatActivity, code: Int) {
    if (Build.VERSION.SDK_INT >= 23)
        activity.requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), code)
}

fun getCacheWorkDirectoryVideo(context: Context): String {
    return context.cacheDir.absolutePath + "/temp.mp4";
}

fun getFinalOutputPath(context: Context): String {
    return context.cacheDir.absolutePath + "/final.mp4"
}

fun saveCacheToInternalStorage(cachePath: String): String {

    val fileName = "Lii_${System.currentTimeMillis()/1000L}.mp4"
    val appVideoDirectory = File(Environment.getExternalStorageDirectory(),"lii")
    if(!appVideoDirectory.exists()) appVideoDirectory.mkdir()

    val fileToSave = File(appVideoDirectory,fileName)
    if(!fileToSave.exists()) fileToSave
    try {
        val outputStream = FileOutputStream(fileToSave)
        val inputStream = FileInputStream(File(cachePath))
        val buffer = ByteArray(1024)
        var len: Int
        while (inputStream.read(buffer).also { len = it } > 0) {
            outputStream.write(buffer, 0, len)
        }
        inputStream.close()
        outputStream.close()
        return "File Saved To Internal Storage!"
    } catch (e: IOException) {
        e.printStackTrace()
        return "Error! Failed To Save"
    }
}