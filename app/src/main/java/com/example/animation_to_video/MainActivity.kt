package com.example.animation_to_video

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.animation_to_video.databinding.ActivityMainBinding
import com.example.animation_to_video.service.VideoService
import com.example.animation_to_video.templatemode.TemplateActivity
import kotlinx.coroutines.*
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var selectedImgUri : Uri? = null
    var processingflag = true
    var selectedTemplate: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        selectedTemplate = intent.getIntExtra(TemplateActivity.SELECTED_TEMPLATE,0)
        binding.progressBar.text = selectedTemplate.toString() + " no template selected!"
        if (savedInstanceState != null) {
            selectedImgUri = savedInstanceState.getParcelable("selectedImgUri")!!
        }

        initView()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("selectedImgUri", selectedImgUri)
    }

    private fun initView() {
        binding.btnAddImage.setOnClickListener {
            if (needsStoragePermission(this@MainActivity)) {
                requestStoragePermission(this@MainActivity, CODE_IMAGE_SEARCH)
            } else {
                performImagesSearch(this@MainActivity, CODE_IMAGE_SEARCH)
            }
        }

        binding.btnCreateVideo.setOnClickListener {
            requestEncodeImages()
            startCounting()
        }

        binding.playButton.setOnClickListener {
            playPreview()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CODE_IMAGE_SEARCH && resultCode == Activity.RESULT_OK)  selectedImgUri = data?.data
        else if (requestCode == CODE_ENCODING_FINISHED)  {
            processingflag = false
            Toast.makeText(this,"Done",Toast.LENGTH_SHORT).show()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0])
            Toast.makeText(this, getString(R.string.warn_no_storage_permission), Toast.LENGTH_LONG)
                .show()
        } else {
            when (requestCode) {
                CODE_IMAGE_SEARCH -> {
                    performImagesSearch(
                        this@MainActivity, CODE_IMAGE_SEARCH)
                }

            }
        }
    }


    private fun requestEncodeImages() {
        if (selectedImgUri != null) {
            val outPath = getOutputPath()
            val contentUri = FileProvider.getUriForFile(this, "com.example.animation_to_video.provider", File(outPath))
            val intent = Intent(this, VideoService::class.java).apply {
                action = VideoService.ACTION_ENCODE_IMAGES

                putExtra(VideoService.KEY_OUT_PATH, getOutputPath())
                putExtra(VideoService.FINAL_VIDEO_PATH,getFinalOutputPath())
                putExtra(VideoService.CONTENT_URI,contentUri.toString())
                putExtra(VideoService.KEY_IMAGES, selectedImgUri.toString())
                putExtra(TemplateActivity.SELECTED_TEMPLATE,selectedTemplate%4)

                // hard coded Opacity
                putExtra(VideoService.BACKGROUND_OPACITY,0.5f)

                // We want this Activity to get notified once the encoding has finished
                val pi = createPendingResult(CODE_ENCODING_FINISHED, intent, 0)
                putExtra(VideoService.KEY_RESULT_INTENT, pi)
            }

            startService(intent)

        } else {
            Toast.makeText(this@MainActivity, getString(R.string.err_one_file), Toast.LENGTH_LONG)
                .show()
        }
    }

    fun startCounting() {

        // code for time counting --------------------------------------------
        processingflag = true

        var cnt = 0
        val defaultScope = CoroutineScope(Dispatchers.Default)
        defaultScope.launch {
            while (processingflag){
                cnt += 2
                withContext(Dispatchers.Main){
                    binding.progressBar.text = (cnt/10).toString()+"."+(cnt%10).toString()
                }
                delay(200)
            }
        }
        // ------------------------------------------------------------------------

    }


    private fun playPreview() {
        val outFile = File(getFinalOutputPath())
        if (outFile.exists()) {
            val uri =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    FileProvider.getUriForFile(this, "$packageName.provider", outFile)
                else
                    Uri.parse(outFile.absolutePath)

            val intent = Intent(Intent.ACTION_VIEW, uri)
                .setDataAndType(uri,"video/*")
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .setDataAndType(uri, "video/mp4")

            startActivityForResult(intent, CODE_THUMB)

        } else {
            Toast.makeText(this, getString(R.string.app_name), Toast.LENGTH_LONG).show()
        }
    }

    private fun getFinalOutputPath(): String {
        return cacheDir.absolutePath + "/" + FINAL_VIDEO_NAME
    }

    private fun getOutputPath(): String {
        return cacheDir.absolutePath + "/" + OUT_FILE_NAME
    }

    companion object {
        const val TAG = "MainActivity"

        const val CODE_IMAGE_SEARCH = 1110
        const val CODE_ENCODING_FINISHED = 1111
        const val CODE_THUMB = 1112
        const val OUT_FILE_NAME = "out.mp4"
        const val FINAL_VIDEO_NAME = "final.mp4"
    }
}