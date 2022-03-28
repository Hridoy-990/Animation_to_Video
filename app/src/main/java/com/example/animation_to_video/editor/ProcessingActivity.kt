package com.example.animation_to_video.editor

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.animation_to_video.*
import com.example.animation_to_video.databinding.ActivityProcessingBinding
import com.example.animation_to_video.datamodel.TemplatePropertiesData
import com.example.animation_to_video.service.VideoService
import java.io.File

class ProcessingActivity : AppCompatActivity() {

    private lateinit var binding : ActivityProcessingBinding

    private lateinit var templatePropertiesData: TemplatePropertiesData
    var progressDotCount: Int = 0
    private var processingFinishedFlag: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProcessingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showProcessingView()

        setListeners()

        templatePropertiesData = intent.getSerializableExtra(AppConstants.FINAL_TEMPLATE_DATA) as TemplatePropertiesData

        precess()

    }

    private fun showProcessingView() {
        binding.vvProcessedVideo.visibility = View.GONE
        binding.btPlayProcessedVideo.visibility = View.GONE
        binding.btDoneEditing.visibility = View.GONE
        binding.pbProcessing.visibility = View.VISIBLE
        binding.tvProgressPercent.visibility = View.VISIBLE
        binding.tvProgressName.visibility = View.VISIBLE
        binding.tvDotLoading.visibility = View.VISIBLE
    }

    private fun showProcessDoneView() {
        binding.pbProcessing.visibility = View.GONE
        binding.tvProgressPercent.visibility = View.GONE
        binding.tvProgressName.visibility = View.GONE
        binding.tvDotLoading.visibility = View.GONE
        binding.vvProcessedVideo.visibility = View.VISIBLE
        binding.btPlayProcessedVideo.visibility = View.VISIBLE
        binding.btDoneEditing.visibility = View.VISIBLE
    }

    private fun setListeners() {
        binding.btPlayProcessedVideo.setOnClickListener {
            binding.vvProcessedVideo.setVideoURI(Uri.parse(getFinalOutputPath(this)))
            binding.vvProcessedVideo.start()
        }
        binding.btDoneEditing.setOnClickListener {
            this.finish()
        }
    }

    private fun precess() {
        val cacheWorkDir = getCacheWorkDirectoryVideo(this)
        val contentUri = FileProvider.getUriForFile(this, "com.example.animation_to_video.provider", File(cacheWorkDir))

        val intent = Intent(this, VideoService::class.java).apply {
            action = VideoService.ACTION_ENCODE_VIDEO

            putExtra(VideoService.CONTENT_URI, contentUri.toString())
            putExtra(VideoService.KEY_OUT_PATH, cacheWorkDir)
            putExtra(VideoService.FINAL_VIDEO_PATH, getFinalOutputPath(this@ProcessingActivity))
            putExtra(MainActivity.USER_INPUT_DATA, templatePropertiesData)

            val pi = createPendingResult(MainActivity.CODE_ENCODING_FINISHED, intent, 0)
            putExtra(VideoService.KEY_RESULT_INTENT, pi)

            val progressResult = createPendingResult(MainActivity.PROGRESS_RESULT,intent,0)
            putExtra(VideoService.PROGRESS_REULT_INTENT_KEY,progressResult)

            val backgroundProgressResult = createPendingResult(MainActivity.BACKGROUND_PROGRESS_RESULT,intent,0)
            putExtra(VideoService.BACKGROUND_PROGRESS_INTENT_KEY,backgroundProgressResult)
        }

        startService(intent)
    }

    override fun onBackPressed() {
        if(processingFinishedFlag) {
            super.onBackPressed()
            this.finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            MainActivity.CODE_ENCODING_FINISHED -> {
                Toast.makeText(this,"Done", Toast.LENGTH_SHORT).show()
                val finalVideoPath = getFinalOutputPath(this)
                val saveMessage = saveCacheToInternalStorage(finalVideoPath) // save for external use
                Toast.makeText(this,saveMessage, Toast.LENGTH_LONG).show()
                showProcessDoneView()
                binding.vvProcessedVideo.setVideoURI(Uri.parse(finalVideoPath))
                binding.vvProcessedVideo.seekTo(1)
                processingFinishedFlag = true
            }
            MainActivity.PROGRESS_RESULT -> {

                updateProgress("Drawing text",resultCode)
                //textView.setText("Drawing text : "+ resultCode.toString()+"%")
                //progressText.setText("Drawing text : "+ resultCode.toString()+"%")
            }
            MainActivity.BACKGROUND_PROGRESS_RESULT -> {
                updateProgress("Preparing Background ",resultCode)
                //textView.setText("Preparing background : " + resultCode.toString()+"%")
            }
        }
    }

    fun updateProgress(progressName: String, progressPercent: Int) {
        if(progressPercent > 100) return
        binding.pbProcessing.progress = progressPercent
        binding.tvProgressPercent.text = "$progressPercent%"
        binding.tvProgressName.text = "$progressName"

        binding.tvDotLoading.text = " "
        progressDotCount = (progressDotCount + 1) % 4
        for(i in 0 until progressDotCount) {
            binding.tvDotLoading.append(".")
        }
    }
}