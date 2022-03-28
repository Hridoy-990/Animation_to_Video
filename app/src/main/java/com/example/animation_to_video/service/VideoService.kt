package com.example.animation_to_video.service

import android.app.IntentService
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.animation_to_video.MainActivity
import com.example.animation_to_video.animation.AnimationOverlayProcessor
import com.example.animation_to_video.datamodel.TemplatePropertiesData
import com.example.animation_to_video.templatemode.TemplateActivity
import com.example.animation_to_video.userinputtemplate.UserInputForTemplate
import com.example.animation_to_video.videoprocessing.Encoder
import kotlinx.coroutines.*
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

       // val userInputForTemplate: UserInputForTemplate = intent.getSerializableExtra(MainActivity.USER_INPUT_DATA) as UserInputForTemplate
        val templatePropertiesData: TemplatePropertiesData = intent.getSerializableExtra(MainActivity.USER_INPUT_DATA) as TemplatePropertiesData
       // val userUri = Uri.parse(userInputForTemplate.logoUri)
        val outPath = intent.getStringExtra(KEY_OUT_PATH)
        val finalPath = intent.getStringExtra(FINAL_VIDEO_PATH)
        val contentUri = intent.getStringExtra(CONTENT_URI)
       // val selectedTemplate = userInputForTemplate.selectedTemplate
        val backgroundOpacity = templatePropertiesData.backgroundBrightness

        val progressResult = intent.getParcelableExtra<PendingIntent>(PROGRESS_REULT_INTENT_KEY)
        val backgroundProgressResult = intent.getParcelableExtra<PendingIntent>(BACKGROUND_PROGRESS_INTENT_KEY)

        var videoPath = outPath
       /* val videoFile = File(videoPath)
        if (videoFile.exists()) videoFile.delete()
        Encoder().encode(videoPath!!, imageUri , contentResolver)*/

        var inProgressFlag = true
        var backgroundInProgressFlag = true
        if(templatePropertiesData.backgroundType == "video") backgroundInProgressFlag = false

        val bitmapOverlayProcessor = AnimationOverlayProcessor()
        val backgroundEncoder = Encoder()

        if(templatePropertiesData.backgroundType == "video") {

            val coroutineScope = CoroutineScope(Dispatchers.Default)
            coroutineScope.launch {
                while (inProgressFlag) {
                    withContext(Dispatchers.Default) {
                        progressResult!!.send(bitmapOverlayProcessor.getProgress())
                    }
                    delay(200)
                }
            }

            bitmapOverlayProcessor.process(applicationContext, finalPath!!,
                contentResolver.openFileDescriptor(Uri.parse(templatePropertiesData.backgroundVideoUri),"r")!!.fileDescriptor,
                templatePropertiesData)
            inProgressFlag = false
        }
        else {

            val coroutineScopeForBackground = CoroutineScope(Dispatchers.Default)
            coroutineScopeForBackground.launch {
                while (backgroundInProgressFlag) {
                    withContext(Dispatchers.Default) {
                        backgroundProgressResult!!.send(backgroundEncoder.getProgress())
                    }
                    delay(200)
                }
            }
            // get background video ready ----------------------------------------------------
            val videoFile = File(outPath)
            if (videoFile.exists()) videoFile.delete()
            backgroundEncoder.encode(outPath!!, Uri.parse(templatePropertiesData.backgroundImageUri), contentResolver, backgroundOpacity,templatePropertiesData)

            backgroundInProgressFlag = false
            // ---------------------------------------------------------------------------------


            val coroutineScope = CoroutineScope(Dispatchers.Default)
            coroutineScope.launch {
                while (inProgressFlag) {
                    withContext(Dispatchers.Default) {
                        progressResult!!.send(bitmapOverlayProcessor.getProgress())
                    }
                    delay(200)
                }
            }
            // ----------- do effects -----------------------------------------------
            bitmapOverlayProcessor.process(applicationContext, finalPath!!,
                contentResolver.openFileDescriptor(Uri.parse(contentUri),"r")!!.fileDescriptor,
                templatePropertiesData)
            inProgressFlag = false
            // ----------------------------------------------------------------------------
        }

        val pi = intent.getParcelableExtra<PendingIntent>(KEY_RESULT_INTENT)
        pi?.send()

    }

    companion object {

        val TAG = this::class.java.simpleName
        const val ACTION_ENCODE_VIDEO = "com.example.animation_to_video.VIDEO"
        const val KEY_LOGO = "com.example.animation_to_video.key.IMAGES"
        const val KEY_OUT_PATH = "com.example.animation_to_video.key.OUT_PATH"
        const val KEY_RESULT_INTENT = "com.example.animation_to_video.key.RESULT_INTENT"
        const val CONTENT_URI = "com.example.animation_to_video.key.CONTENT_URI"
        const val FINAL_VIDEO_PATH = "com.example.animation_to_video.key.FINAL_VIDEO_URI"
        const val KEY_FPS = "framerate"
        const val BACKGROUND_OPACITY = "background-opacity"
        const val PROGRESS_REULT_INTENT_KEY = "com.example.animation_to_video.PROGRESS_RESULT_KEY"
        const val BACKGROUND_PROGRESS_INTENT_KEY = "com.example.animation_to_video.BACKGROUND_PROGRESS_INTENT_KEY"
    }


}