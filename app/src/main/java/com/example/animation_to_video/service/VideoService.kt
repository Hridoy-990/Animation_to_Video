package com.example.animation_to_video.service

import android.app.IntentService
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import com.example.animation_to_video.animation.AnimationOverlayProcessor
import com.example.animation_to_video.videoprocessing.Encoder
import java.io.File

/**
 * @author Md Jahirul Islam Hridoy
 * Created on 21,March,2022
 */
class VideoService : IntentService(TAG) {

    override fun onHandleIntent(p0: Intent?) {
        when(p0?.action) {
            ACTION_ENCODE_IMAGES -> encodeImages(p0)
        }
    }

    private fun encodeImages(intent: Intent) {

        val imageUri = Uri.parse(intent.getStringExtra(KEY_IMAGES))
        val outPath = intent.getStringExtra(KEY_OUT_PATH)
        val finalPath = intent.getStringExtra(FINAL_VIDEO_PATH)
        val contentUri = intent.getStringExtra(CONTENT_URI)

        var videoPath = outPath
        val videoFile = File(videoPath)
        if (videoFile.exists()) videoFile.delete()
        Encoder().encode(videoPath!!, imageUri , contentResolver)

        AnimationOverlayProcessor().process(applicationContext , finalPath!!, contentResolver.openFileDescriptor(Uri.parse(contentUri), "r")!!.fileDescriptor)
        // Notify MainActivity that we're done
        val pi = intent.getParcelableExtra<PendingIntent>(KEY_RESULT_INTENT)
        pi?.send()

    }

    companion object {

        val TAG = this::class.java.simpleName
        const val ACTION_ENCODE_IMAGES = "com.example.animation_to_video.ENCODE_IMAGES"
        const val KEY_IMAGES = "com.example.animation_to_video.key.IMAGES"
        const val KEY_OUT_PATH = "com.example.animation_to_video.key.OUT_PATH"
        const val KEY_RESULT_INTENT = "com.example.animation_to_video.key.RESULT_INTENT"
        const val CONTENT_URI = "com.example.animation_to_video.key.CONTENT_URI"
        const val FINAL_VIDEO_PATH = "com.example.animation_to_video.key.FINAL_VIDEO_URI"
        const val KEY_FPS = "framerate"
    }


}