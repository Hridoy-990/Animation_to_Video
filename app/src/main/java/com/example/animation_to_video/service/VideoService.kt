package com.example.animation_to_video.service

import android.app.IntentService
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.animation_to_video.MainActivity
import com.example.animation_to_video.animation.AnimationOverlayProcessor
import com.example.animation_to_video.templatemode.TemplateActivity
import com.example.animation_to_video.userinputtemplate.UserInputForTemplate
import com.example.animation_to_video.videoprocessing.Encoder
import java.io.File

/**
 * @author Md Jahirul Islam Hridoy
 * Created on 21,March,2022
 */
class VideoService : IntentService(TAG) {

    override fun onHandleIntent(p0: Intent?) {
        when(p0?.action) {
            ACTION_ENCODE_VIDEO -> encodeImages(p0)
        }
    }

    private fun encodeImages(intent: Intent) {

        val userInputForTemplate: UserInputForTemplate = intent.getSerializableExtra(MainActivity.USER_INPUT_DATA) as UserInputForTemplate
        val userUri = Uri.parse(userInputForTemplate.logoUri)
        val outPath = intent.getStringExtra(KEY_OUT_PATH)
        val finalPath = intent.getStringExtra(FINAL_VIDEO_PATH)
        val contentUri = intent.getStringExtra(CONTENT_URI)
        val selectedTemplate = userInputForTemplate.selectedTemplate
        val backgroundOpacity = userInputForTemplate.backgroundBrightness

        Log.e(TAG, "encodeImages: ${userInputForTemplate.backgroundImageUri}")
        Log.e(TAG, "encodeImages: ${Uri.parse(userInputForTemplate.backgroundImageUri)}")

        var videoPath = outPath
       /* val videoFile = File(videoPath)
        if (videoFile.exists()) videoFile.delete()
        Encoder().encode(videoPath!!, imageUri , contentResolver)*/

        if(userInputForTemplate.backgroundType == "video") {
            AnimationOverlayProcessor().process(applicationContext,finalPath!!,
                contentResolver.openFileDescriptor(Uri.parse(userInputForTemplate.backgroundVideoUri),"r")!!.fileDescriptor, userInputForTemplate)
        }

        else {
            // get background video ready ----------------------------------------------------
            val videoFile = File(outPath)
            if (videoFile.exists()) videoFile.delete()
            Encoder().encode(outPath!!, Uri.parse(userInputForTemplate.backgroundImageUri), contentResolver, backgroundOpacity,userInputForTemplate)
            // ---------------------------------------------------------------------------------


            // ----------- do effects -----------------------------------------------

            AnimationOverlayProcessor().process(applicationContext,finalPath!!,
                contentResolver.openFileDescriptor(Uri.parse(contentUri),"r")!!.fileDescriptor, userInputForTemplate)
            // ----------------------------------------------------------------------------
        }

        val pi = intent.getParcelableExtra<PendingIntent>(KEY_RESULT_INTENT)
        pi?.send()

    }

    companion object {

        val TAG = this::class.java.simpleName
        const val ACTION_ENCODE_VIDEO = "com.example.animation_to_video.VIDEO"
        const val KEY_IMAGES = "com.example.animation_to_video.key.IMAGES"
        const val KEY_OUT_PATH = "com.example.animation_to_video.key.OUT_PATH"
        const val KEY_RESULT_INTENT = "com.example.animation_to_video.key.RESULT_INTENT"
        const val CONTENT_URI = "com.example.animation_to_video.key.CONTENT_URI"
        const val FINAL_VIDEO_PATH = "com.example.animation_to_video.key.FINAL_VIDEO_URI"
        const val KEY_FPS = "framerate"
        const val BACKGROUND_OPACITY = "background-opacity"
    }


}