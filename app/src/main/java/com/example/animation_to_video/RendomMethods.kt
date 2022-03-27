package com.example.animation_to_video

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity

/**
 * @author Md Jahirul Islam Hridoy
 * Created on 23,March,2022
 */

fun needsPermission(context: Context): Boolean {
    return Build.VERSION.SDK_INT >= 23 && context.checkSelfPermission(
        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
}

fun requestPermission(activity: AppCompatActivity, code: Int) {
    if (Build.VERSION.SDK_INT >= 23)
        activity.requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), code)
}