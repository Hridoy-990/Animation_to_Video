package com.example.animation_to_video.videoprocessing

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.net.Uri
import android.opengl.*
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.Surface
import com.example.animation_to_video.datamodel.TemplatePropertiesData
import com.example.animation_to_video.getBestSupportedResolution
import com.example.animation_to_video.getSizeByHeight
import com.example.animation_to_video.userinputtemplate.UserInputForTemplate
import java.lang.Exception
import java.lang.RuntimeException

/**
 * @author Md Jahirul Islam Hridoy
 * Created on 21,March,2022
 */
class Encoder() {
    private var encoder: MediaCodec? = null
    private var muxer: MediaMuxer? = null
    private var mime = "video/avc"
    private var trackIndex = -1
    private var presentationTimeUs = 0L
    private val totalTime = 10000000L
    private var frameRate = 24+12
    private val timeoutUs = 10000L
    private val bufferInfo = MediaCodec.BufferInfo()
    private var size: Size? = null
    private var eglDisplay: EGLDisplay? = null
    private var eglContext: EGLContext? = null
    private var eglSurface: EGLSurface? = null
    private var surface: Surface? = null


    fun encode(outVideoFilePath: String, backgroundImageUri: Uri, contentResolver: ContentResolver , backgroundOpacity: Float,
               templatePropertiesData: TemplatePropertiesData
    ) {
        try {
            initEncoder(outVideoFilePath, backgroundImageUri, contentResolver,templatePropertiesData)

            createVideo(backgroundImageUri, contentResolver, backgroundOpacity,templatePropertiesData)
        } catch (e: Exception) {
            Log.e(TAG, "Encoding failed: " + e)
        } finally {
            releaseEncoder()
        }
    }

    private fun initEncoder(outVideoFilePath: String, backgroundImageUri: Uri,
                            contentResolver: ContentResolver,
                            templatePropertiesData: TemplatePropertiesData) {
        encoder = MediaCodec.createEncoderByType(mime)
       // size = getSupportedSize(backgroundImageUri, contentResolver)


        size = getSizeByHeight(templatePropertiesData.videoResolution)

        val format = getFormat(size!!)

        encoder!!.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        initEgl()
        encoder!!.start()
        muxer = MediaMuxer(outVideoFilePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
    }

    private fun getSupportedSize(inBitmapUri: Uri, contentResolver: ContentResolver): Size {
        val first = MediaStore.Images.Media.getBitmap(contentResolver, inBitmapUri)
        return getBestSupportedResolution(encoder!!, mime, Size(first.width, first.height))
    }

    private fun getFormat(size: Size): MediaFormat {
        val format = MediaFormat.createVideoFormat(mime, size.width, size.height)
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)
        format.setInteger(MediaFormat.KEY_BIT_RATE, 2000000)
        format.setInteger(MediaFormat.KEY_FRAME_RATE, 24)
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)

        return format
    }

    private fun initEgl() {
        surface = encoder!!.createInputSurface()
        eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
        if (eglDisplay == EGL14.EGL_NO_DISPLAY)
            throw RuntimeException("eglDisplay == EGL14.EGL_NO_DISPLAY: "
                    + GLUtils.getEGLErrorString(EGL14.eglGetError()))

        val version = IntArray(2)
        if (!EGL14.eglInitialize(eglDisplay, version, 0, version, 1))
            throw RuntimeException("eglInitialize(): " + GLUtils.getEGLErrorString(EGL14.eglGetError()))

        val attribList = intArrayOf(
            EGL14.EGL_RED_SIZE, 8,
            EGL14.EGL_GREEN_SIZE, 8,
            EGL14.EGL_BLUE_SIZE, 8,
            EGL14.EGL_ALPHA_SIZE, 8,
            EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
            EGLExt.EGL_RECORDABLE_ANDROID, 1,
            EGL14.EGL_NONE
        )
        val configs = arrayOfNulls<EGLConfig>(1)
        val nConfigs = IntArray(1)
        EGL14.eglChooseConfig(eglDisplay, attribList, 0, configs, 0, configs.size, nConfigs, 0)

        var err = EGL14.eglGetError()
        if (err != EGL14.EGL_SUCCESS)
            throw RuntimeException(GLUtils.getEGLErrorString(err))

        val ctxAttribs = intArrayOf(
            EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
            EGL14.EGL_NONE
        )
        eglContext = EGL14.eglCreateContext(eglDisplay, configs[0], EGL14.EGL_NO_CONTEXT, ctxAttribs, 0)

        err = EGL14.eglGetError()
        if (err != EGL14.EGL_SUCCESS)
            throw RuntimeException(GLUtils.getEGLErrorString(err))

        val surfaceAttribs = intArrayOf(
            EGL14.EGL_NONE
        )
        eglSurface = EGL14.eglCreateWindowSurface(eglDisplay, configs[0], surface, surfaceAttribs, 0)
        err = EGL14.eglGetError()
        if (err != EGL14.EGL_SUCCESS)
            throw RuntimeException(GLUtils.getEGLErrorString(err))

        if (!EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext))
            throw RuntimeException("eglMakeCurrent(): " + GLUtils.getEGLErrorString(EGL14.eglGetError()))
    }



    private fun createVideo(logoUri: Uri, contentResolver: ContentResolver,
                            backgroundOpacity: Float, templatePropertiesData: TemplatePropertiesData) {
        val renderer = TextureRenderer(backgroundOpacity)
        val effectsFactory = EffectMvp()

        val bitmap = Bitmap.createScaledBitmap(
            MediaStore.Images.Media.getBitmap(contentResolver, logoUri),
            size!!.width,size!!.height,false)

        if(templatePropertiesData.backgroundType == "color"){
            val rect = Rect(0,0,bitmap.width,bitmap.height)
            val canvas = Canvas(bitmap)
            val paint = Paint()
            paint.color = templatePropertiesData.backgroundColor
            canvas.drawRect(rect,paint)
        }

        while (presentationTimeUs < totalTime){
            drainEncoder(false)


            val effectMVP = effectsFactory.getMvp(7,presentationTimeUs,totalTime,size!!)

            renderer.draw(size!!.width, size!!.height, bitmap,effectMVP)
            EGLExt.eglPresentationTimeANDROID(eglDisplay, eglSurface, presentationTimeUs * 1000)
            EGL14.eglSwapBuffers(eglDisplay, eglSurface)
        }

        drainEncoder(true)
    }

    var totalFrames = 0

    private fun drainEncoder(endOfStream: Boolean) {
        if (endOfStream)
            encoder!!.signalEndOfInputStream()

        while (true) {
            val outBufferId = encoder!!.dequeueOutputBuffer(bufferInfo, timeoutUs)

            if (outBufferId >= 0) {
                val encodedBuffer = encoder!!.getOutputBuffer(outBufferId)
                bufferInfo.presentationTimeUs = presentationTimeUs
                muxer!!.writeSampleData(trackIndex, encodedBuffer!!, bufferInfo)

                presentationTimeUs += 1000000/frameRate
                Log.e(TAG, "drainEncoder: $frameRate -- $presentationTimeUs -- no : ${totalFrames++}")

                encoder!!.releaseOutputBuffer(outBufferId, false)
                if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0)
                    break
            } else if (outBufferId == MediaCodec.INFO_TRY_AGAIN_LATER) {
                if (!endOfStream)
                    break
            } else if (outBufferId == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                trackIndex = muxer!!.addTrack(encoder!!.outputFormat)
                muxer!!.start()
            }
        }
    }

    private fun releaseEncoder() {
        encoder?.stop()
        encoder?.release()
        encoder = null

        releaseEgl()

        muxer?.stop()
        muxer?.release()
        muxer = null

        size = null
        trackIndex = -1
        presentationTimeUs = 0L
    }

    private fun releaseEgl() {
        if (eglDisplay != EGL14.EGL_NO_DISPLAY) {
            EGL14.eglDestroySurface(eglDisplay, eglSurface)
            EGL14.eglDestroyContext(eglDisplay, eglContext)
            EGL14.eglReleaseThread()
            EGL14.eglTerminate(eglDisplay)
        }

        surface?.release()
        surface = null

        eglDisplay = EGL14.EGL_NO_DISPLAY
        eglContext = EGL14.EGL_NO_CONTEXT
        eglSurface = EGL14.EGL_NO_SURFACE
    }

    fun getProgress(): Int {
        return (100f*(presentationTimeUs.toFloat() / totalTime.toFloat())).toInt()
    }

    companion object {
        const val TAG = "Encoder"
    }
}