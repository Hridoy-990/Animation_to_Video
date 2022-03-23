package com.example.animation_to_video

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.animation_to_video.databinding.ActivityMainBinding
import com.example.animation_to_video.service.VideoService
import com.example.animation_to_video.templatemode.TemplateActivity
import com.example.animation_to_video.userinputtemplate.UserInputForTemplate
import kotlinx.coroutines.*
import yuku.ambilwarna.AmbilWarnaDialog
import java.io.File

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private var selectedImgUri : Uri? = null
    lateinit var userInputForTemplate: UserInputForTemplate
    var processingflag = true


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userInputForTemplate = intent.getSerializableExtra(TemplateActivity.TEMPLATE_DATA) as UserInputForTemplate

        if (savedInstanceState != null) {
            selectedImgUri = savedInstanceState.getParcelable("selectedImgUri")!!
            userInputForTemplate.logoUri = selectedImgUri.toString()
        }

        when (userInputForTemplate.selectedTemplate) {
            0 -> binding.previewImageView.setImageResource(R.drawable.tmpbg0)
            1 -> binding.previewImageView.setImageResource(R.drawable.tmpbg1)
            2 -> binding.previewImageView.setImageResource(R.drawable.tmpbg2)
            2 + 1 -> binding.previewImageView.setImageResource(R.drawable.tmpbg3)
            4 -> binding.previewImageView.setImageResource(R.drawable.tmpbg4)
            5 -> binding.previewImageView.setImageResource(R.drawable.ic_video)
        }

        binding.progressText.text = userInputForTemplate.selectedTemplate.toString() + " no template selected!"
        initUserInputAsTemplate()

        initView()

    }

    private fun initUserInputAsTemplate() {

        //logoUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.video)
        userInputForTemplate.logoUri = selectedImgUri.toString()

        // default video
        val outPath = getOutputPath()
        val contentUri = FileProvider.getUriForFile(this, "com.example.animation_to_video.provider", File(outPath))

        userInputForTemplate.logoUri = contentUri.toString()

        initDataFromTemplateToView()
    }

    private fun initDataFromTemplateToView() {

        binding.bigEditText.setText(userInputForTemplate.bigText)
        binding.bigEditText.setTextColor(userInputForTemplate.bigTextColor)
        binding.smallEditText.setText(userInputForTemplate.smallText)
        binding.smallEditText.setTextColor(userInputForTemplate.smallTextColor)
        binding.barColor.setTextColor(userInputForTemplate.barColor)

        if(userInputForTemplate.smallTextColor == Color.TRANSPARENT) {
            binding.smallEditText.visibility = View.GONE
            binding.smallTextColor.visibility = View.GONE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("selectedImgUri", selectedImgUri)
    }

    private fun initView() {
        binding.addImageButton.setOnClickListener {
            if (needsStoragePermission(this@MainActivity)) {
                requestStoragePermission(this@MainActivity, CODE_IMAGE_SEARCH)
            } else {
                //performImagesSearch(this@MainActivity, CODE_IMAGE_SEARCH)
                val gallery = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                )
                startActivityForResult(gallery, 101)
            }
        }

        binding.createButton.setOnClickListener {
            requestEncodeImages()
            startCounting()
            binding.createButton.isEnabled = false
        }

        binding.playButton.setOnClickListener {
            playPreview()
        }

        binding.bigTextColor.setOnClickListener { colorPicker(0) }
        binding.smallTextColor.setOnClickListener { colorPicker(1) }
        binding.barColor.setOnClickListener { colorPicker(2) }

        // spinner for resolution --------------------------------------------------
        val resolutionSpinnerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.resolution_values,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            binding.resolutionSpinner.adapter = adapter
        }

        val defaultResolutionPosition = resolutionSpinnerAdapter.getPosition("720")
        binding.resolutionSpinner.setSelection(defaultResolutionPosition)
        userInputForTemplate.videoResolution = 720
        binding.resolutionSpinner.onItemSelectedListener = this
        // ------------------------------------------------------------------------

        // ----------------------------------------------------------

        // spinner background type --------------------------------------

        val backgroundTypeSpinnerAdapter = ArrayAdapter.createFromResource(
            this, R.array.background_types, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.backgroundTypeSpinner.adapter = adapter
        }

        val defaultBackgroundTypePosition = backgroundTypeSpinnerAdapter.getPosition("video")
        binding.backgroundTypeSpinner.setSelection((defaultBackgroundTypePosition))
        userInputForTemplate.backgroundType = "video"
        binding.backgroundTypeSpinner.onItemSelectedListener = this

        // ------------------------------------------------------------------------

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            selectedImgUri = data?.data
            userInputForTemplate.logoUri = selectedImgUri.toString()
        }
        else if (requestCode == CODE_ENCODING_FINISHED)  {
            processingflag = false
            Toast.makeText(this,"Done",Toast.LENGTH_SHORT).show()
            binding.createButton.isEnabled = true
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

            generateUserInput() // get user input from edit text and set to userInputForTemplate

            Log.e(TAG, "createVideo: ${userInputForTemplate.bigText}")

            val intent = Intent(this, VideoService::class.java).apply {
                action = VideoService.ACTION_ENCODE_IMAGES

                putExtra(VideoService.KEY_OUT_PATH, getOutputPath())
                putExtra(VideoService.FINAL_VIDEO_PATH,getFinalOutputPath())
                putExtra(VideoService.CONTENT_URI,contentUri.toString())
                putExtra(USER_INPUT_DATA,userInputForTemplate)

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

    private fun generateUserInput() {
        userInputForTemplate.bigText = binding.bigEditText.text.toString()
        if(binding.smallEditText.visibility == View.VISIBLE) {
            userInputForTemplate.smallText = binding.smallEditText.text.toString()
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
                    binding.progressText.text = (cnt/10).toString()+"."+(cnt%10).toString()
                }
                delay(200)
            }
        }
        // ------------------------------------------------------------------------

    }


    private fun playPreview() {
        val outFile = File(getFinalOutputPath())
        if (outFile.exists()) {
            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)  FileProvider.getUriForFile(this, "$packageName.provider", outFile)
                      else Uri.parse(outFile.absolutePath)

            binding.previewImageView.visibility = View.GONE
            binding.previewVideoView.visibility = View.VISIBLE
            binding.previewVideoView.setVideoURI(uri)
            binding.previewVideoView.start()

//            val intent = Intent(Intent.ACTION_VIEW, uri)
//                .setDataAndType(uri,"video/*")
//                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                .setDataAndType(uri, "video/mp4")
//
//            startActivityForResult(intent, CODE_THUMB)

        } else {
            Toast.makeText(this, getString(R.string.app_name), Toast.LENGTH_LONG).show()
        }
    }

    private fun colorPicker(id: Int) {

        val initialColor = 0
        when(id) {
            0 -> userInputForTemplate.bigTextColor
            1 -> userInputForTemplate.smallTextColor
            2 -> userInputForTemplate.barColor
        }

        val dialog = AmbilWarnaDialog(this, initialColor, object :
            AmbilWarnaDialog.OnAmbilWarnaListener {
            override fun onOk(dialog: AmbilWarnaDialog, color: Int) {
                // color is the color selected by the user.
                when (id) {
                    0 -> {
                        userInputForTemplate.bigTextColor = color
                        binding.bigEditText.setTextColor(color)
                    }
                    1 -> {
                        userInputForTemplate.smallTextColor = color
                        binding.smallEditText.setTextColor(color)
                    }
                    2 -> {
                        userInputForTemplate.barColor = color
                        binding.barColor.setTextColor(color)
                    }
                }
            }

            override fun onCancel(dialog: AmbilWarnaDialog) {
                // cancel was selected by the user
            }
        })

        dialog.show()
    }

    private fun getFinalOutputPath(): String {
        return cacheDir.absolutePath + "/" + FINAL_VIDEO_NAME
    }

    private fun getOutputPath(): String {
        return cacheDir.absolutePath + "/" + OUT_FILE_NAME
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if(parent == binding.resolutionSpinner) {
            var selectedResolution = parent.getItemAtPosition(position).toString()
            if(selectedResolution.isEmpty()) selectedResolution = "720"
            userInputForTemplate.videoResolution = selectedResolution.toInt()
        }
        else if(parent == binding.backgroundTypeSpinner) {
            var selectedType = parent.getItemAtPosition(position).toString()
            if(selectedType.isEmpty()) selectedType = "video"
            userInputForTemplate.backgroundType = selectedType
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    companion object {
        const val TAG = "MainActivity"

        const val CODE_IMAGE_SEARCH = 1110
        const val CODE_ENCODING_FINISHED = 1111
        const val CODE_THUMB = 1112
        const val OUT_FILE_NAME = "out.mp4"
        const val USER_INPUT_DATA = "user-input-data"
        const val FINAL_VIDEO_NAME = "final.mp4"
    }


}