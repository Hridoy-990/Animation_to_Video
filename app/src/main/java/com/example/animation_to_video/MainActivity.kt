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
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.FileProvider
import androidx.core.content.FileProvider.getUriForFile
import com.example.animation_to_video.databinding.ActivityMainBinding
import com.example.animation_to_video.datamodel.TemplatePropertiesData
import com.example.animation_to_video.repo.TemplateRepository
import com.example.animation_to_video.service.VideoService
import com.example.animation_to_video.templatemode.TemplateActivity
import com.example.animation_to_video.userinputtemplate.UserInputForTemplate
import kotlinx.coroutines.*
import yuku.ambilwarna.AmbilWarnaDialog
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private var selectedUri : Uri? = null
    private val templateRepository = TemplateRepository()
    lateinit var templatePropertiesData: TemplatePropertiesData
    var selectedTemplate: Int = 0
    var processingflag = true


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getPermission()
        selectedTemplate = intent.getIntExtra(AppConstants.SELECTED_TEMPLATE,0)
        templatePropertiesData = templateRepository.getTemplateData(selectedTemplate)

        Log.e(TAG, "onCreate: calling", )
        if (savedInstanceState != null) {
            selectedUri = savedInstanceState.getParcelable("selectedUri")!!
        }

        when (selectedTemplate) {
            0 -> binding.previewImageView.setImageResource(R.drawable.tmpbg0)
            1 -> binding.previewImageView.setImageResource(R.drawable.tmpbg1)
            2 -> binding.previewImageView.setImageResource(R.drawable.tmpbg2)
            2 + 1 -> binding.previewImageView.setImageResource(R.drawable.tmpbg3)
            4 -> binding.previewImageView.setImageResource(R.drawable.tmpbg4)
            5 -> binding.previewImageView.setImageResource(R.drawable.ic_video)
        }

        binding.progressText.text = selectedTemplate.toString() + " no template selected!"
        initUserInputAsTemplate()

        initView()

    }

    private fun getPermission() {
        if(needsPermission(this@MainActivity)) {
            requestPermission(this@MainActivity,0)
        }
    }

    private fun initUserInputAsTemplate() {

        //logoUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.video)
       // userInputForTemplate.logoUri = selectedUri.toString()

        // default video
        val outPath = getOutputPath()
      //  val contentUri = FileProvider.getUriForFile(this, "com.example.animation_to_video.provider", File(outPath))

        val contentUri = when(selectedTemplate) {
            0 -> getUriForFile(
                this,
                "com.example.animation_to_video.provider",
                File(cacheDir.absolutePath + "/tmpbg0.mp4")
            )
            1 -> getUriForFile(
                this,
                "com.example.animation_to_video.provider",
                File(cacheDir.absolutePath + "/tmpbg1.mp4")
            )
            2 -> getUriForFile(
                this,
                "com.example.animation_to_video.provider",
                File(cacheDir.absolutePath + "/tmpbg2.mp4")
            )
            3 -> getUriForFile(
                this,
                "com.example.animation_to_video.provider",
                File(cacheDir.absolutePath + "/tmpbg3.mp4")
            )
            4 -> getUriForFile(
                this,
                "com.example.animation_to_video.provider",
                File(cacheDir.absolutePath + "/tmpbg4.mp4")
            )
            5 -> getUriForFile(
                this,
                "com.example.animation_to_video.provider",
                File(cacheDir.absolutePath + "/tmpbg4.mp4")
            )
            else -> getUriForFile(
                this,
                "com.example.animation_to_video.provider",
                File(cacheDir.absolutePath + "/tmpbg2.mp4")
            )
        }

        templatePropertiesData.backgroundVideoUri = contentUri.toString()
        templatePropertiesData.backgroundImageUri = "android.resource://com.example.animation_to_video/drawable/sound"


        initDataFromTemplateToView()
    }

    private fun initDataFromTemplateToView() {

        binding.bigEditText.setText(templatePropertiesData.bigTitleData!!.text)
        binding.bigEditText.setTextColor(templatePropertiesData.bigTitleData!!.textColor)
        binding.smallEditText.setText(templatePropertiesData.smallTitleData!!.text)
        binding.smallEditText.setTextColor(templatePropertiesData.smallTitleData!!.textColor)
        //barColor.setTextColor(userInputForTemplate.barColor)
        binding.barColor.setBackgroundColor(templatePropertiesData.lineData!!.lineColor)

        if(templatePropertiesData.smallTitleData!!.textColor == Color.TRANSPARENT) {
            binding.smallEditText.visibility = View.GONE
            binding.smallTextColor.visibility = View.GONE
        }
    }

    private fun setListeners() {

        binding.changeBackgroundButton.setOnClickListener{
            if (needsStoragePermission(this@MainActivity)) {
                requestStoragePermission(this@MainActivity, CODE_MEDIA_SEARCH)
            }
            else {
                // val gallery = Intent(Intent.ACTION_PICK, MediaStore.Image.Media.EXTERNAL_CONTENT_URI)

                when(templatePropertiesData.backgroundType) {
                    "image" -> {
                        val gallery = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )
                        startActivityForResult(gallery, IMAGE_SEARCH)
                        // update called in activity result ok
                    }
                    "video" -> {
                        Log.d(TAG, "setListeners: video")
                        val gallery = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        )
                        startActivityForResult(gallery, VIDEO_SEARCH)
                        // update called in activity result ok
                    }
                    "color" -> {
                        colorPicker(3) // 3 = background color
                        Log.e(TAG, "setListeners: ${templatePropertiesData.backgroundColor}")
                        //updatePreview(2) called in color picker
                    }
                }
            }
        }

        binding.createButton.setOnClickListener {
            createVideo()
            //startCounting()
            binding.createButton.isEnabled = false
            binding.lastVideoView.stopPlayback()
            binding.playLastVideo.isEnabled = false
        }

        binding.playLastVideo.setOnClickListener {
            playLastVideo()
        }

        binding.playBackgroundButton.setOnClickListener {
            playBackground()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("selectedUri", selectedUri)
    }

    private fun initView() {

        setListeners()
        binding.bigTextColor.setOnClickListener { colorPicker(0) }
        binding.smallTextColor.setOnClickListener { colorPicker(1) }
        binding.barColor.setOnClickListener { colorPicker(2) }

        setSpinners()

    }

    private fun setSpinners() {
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
        templatePropertiesData.videoResolution = 720
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
        templatePropertiesData.backgroundType = "video"
        binding.backgroundTypeSpinner.onItemSelectedListener = this

        // ------------------------------------------------------------------------
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_SEARCH && resultCode == Activity.RESULT_OK) {
            templatePropertiesData.backgroundImageUri = (data?.data).toString()
          // selectedUri = data?.data
          //  userInputForTemplate.logoUri = selectedUri.toString()

            // ----------- init view --------------------------------
           playBackground()
            // ------------------------------------------------------
        }
        else if(resultCode == RESULT_OK && requestCode == VIDEO_SEARCH) {
            templatePropertiesData.backgroundImageUri = (data?.data).toString()

            // ----------- init view --------------------------------
            playBackground()
            // ------------------------------------------------------
        }
        else if (requestCode == CODE_ENCODING_FINISHED)  {
            processingflag = false
           binding.progressText.text = "Done"
            binding.createButton.isEnabled = true
            binding.playLastVideo.isEnabled = true
          //  binding.previewVideoView.visibility = View.VISIBLE
            saveVideoToInternalStorage() // save for external use
        }

        else if(requestCode == PROGRESS_RESULT) {
            binding.progressText.text = "Drawing text : "+ resultCode.toString()+"%"
        }
        else if(requestCode == BACKGROUND_PROGRESS_RESULT) {
            binding.progressText.text = "Preparing background : " + resultCode.toString()+"%"
        }

    }

    private fun playBackground() {
        when(templatePropertiesData.backgroundType) {
            "video" -> {
                binding.previewImageView.visibility = View.GONE
                binding.previewVideoView.visibility = View.VISIBLE
                binding.previewVideoView.setVideoURI(Uri.parse(templatePropertiesData.backgroundVideoUri))
                binding.previewVideoView.start()
            }
            "image" -> {
                binding.previewVideoView.visibility = View.GONE
                binding.previewImageView.visibility = View.VISIBLE
                binding.previewImageView.setImageURI(Uri.parse(templatePropertiesData.backgroundImageUri))
            }
            "color" -> {
                binding.previewVideoView.visibility = View.GONE
                binding.previewImageView.visibility = View.VISIBLE
                binding.previewImageView.setImageResource(0)
                binding.previewImageView.setBackgroundColor(templatePropertiesData.backgroundColor)
            }
        }
    }

    private fun playLastVideo() {

        val outFile = File(getFinalOutputPath())
        if (outFile.exists()) {
            val uri =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    FileProvider.getUriForFile(this, "$packageName.provider", outFile)
                else
                    Uri.parse(outFile.absolutePath)

            binding.lastVideoView.setVideoURI(uri)
            binding.lastVideoView.start()

            /*
            val intent = Intent(Intent.ACTION_VIEW, uri)
                    .setDataAndType(uri, "video/*")
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    .setDataAndType(uri, "video/mp4")

            startActivityForResult(intent, CODE_THUMB)

             */
            */

        } else {
            Toast.makeText(this, "No Video found!", Toast.LENGTH_LONG).show()
        }
    }

    private fun saveVideoToInternalStorage() {

        val fileName = "Lii_${System.currentTimeMillis()/1000L}.mp4"
        val appVideoDirectory = File(Environment.getExternalStorageDirectory(),"lii")
        if(!appVideoDirectory.exists()) appVideoDirectory.mkdir()

        val fileToSave = File(appVideoDirectory,fileName)
        if(!fileToSave.exists()) fileToSave
        try {
            val outputStream = FileOutputStream(fileToSave)
            val inputStream = FileInputStream(File(getFinalOutputPath()))
            val buffer = ByteArray(1024)
            var len: Int
            while (inputStream.read(buffer).also { len = it } > 0) {
                outputStream.write(buffer, 0, len)
            }
            inputStream.close()
            outputStream.close()
            Toast.makeText(this, "Video file saved successfully.", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


//    private fun updatePreview(id: Int) {
//        // id -> 0 = image, 1 = video, 2 = color
//        when(id){
//            0 -> {
//                binding.previewImageView.visibility = View.VISIBLE
//                binding.previewVideoView.visibility = View.GONE
//                binding.previewImageView.setImageURI(Uri.parse(userInputForTemplate.backgroundImageUri))
//            }
//            1 -> {
//                binding.previewImageView.visibility = View.GONE
//                binding.previewVideoView.visibility = View.VISIBLE
//                binding.previewVideoView.setVideoURI(Uri.parse(userInputForTemplate.backgroundVideoUri))
//            }
//            2 -> {
//                binding.previewImageView.visibility = View.VISIBLE
//                binding.previewVideoView.visibility = View.GONE
//                binding.previewImageView.setImageResource(0)
//                binding.previewImageView.setBackgroundColor(userInputForTemplate.backgroundColor)
//                Log.e(TAG, "updatePreview: ${userInputForTemplate.backgroundColor}")
//            }
//        }
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0])
//            Toast.makeText(this, getString(R.string.warn_no_storage_permission), Toast.LENGTH_LONG)
//                .show()
//        } else {
//            when (requestCode) {
//                CODE_IMAGE_SEARCH -> {
//                    performImagesSearch(
//                        this@MainActivity, CODE_IMAGE_SEARCH)
//                }
//
//            }
//        }
//    }


    private fun createVideo() {
            val outPath = getOutputPath()
            val contentUri = getUriForFile(this, "com.example.animation_to_video.provider", File(outPath))

            generateUserInput() // get user input from edit text and set to userInputForTemplate



            val intent = Intent(this, VideoService::class.java).apply {
                action = VideoService.ACTION_ENCODE_VIDEO

                putExtra(VideoService.KEY_OUT_PATH, getOutputPath())
                putExtra(VideoService.FINAL_VIDEO_PATH,getFinalOutputPath())
                putExtra(VideoService.CONTENT_URI,contentUri.toString())
                putExtra(USER_INPUT_DATA,templatePropertiesData)

                // We want this Activity to get notified once the encoding has finished
                val pi = createPendingResult(CODE_ENCODING_FINISHED, intent, 0)
                putExtra(VideoService.KEY_RESULT_INTENT, pi)

                val progressResult = createPendingResult(PROGRESS_RESULT,intent,0)
                putExtra(VideoService.PROGRESS_REULT_INTENT_KEY,progressResult)

                val backgroundProgressResult = createPendingResult(BACKGROUND_PROGRESS_RESULT,intent,0)
                putExtra(VideoService.BACKGROUND_PROGRESS_INTENT_KEY,backgroundProgressResult)
            }

            startService(intent)

    }

    private fun generateUserInput() {
        templatePropertiesData.bigTitleData!!.text = binding.bigEditText.text.toString()
        if(binding.smallEditText.visibility == View.VISIBLE) {
            templatePropertiesData.smallTitleData!!.text = binding.smallEditText.text.toString()
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
            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)  getUriForFile(this, "$packageName.provider", outFile)
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
        val dialog = AmbilWarnaDialog(this, initialColor, object :
            AmbilWarnaDialog.OnAmbilWarnaListener {
            override fun onOk(dialog: AmbilWarnaDialog, color: Int) {
                // color is the color selected by the user.
                when (id) {
                    0 -> {
                        templatePropertiesData.bigTitleData!!.textColor = color
                        binding.bigEditText.setTextColor(color)
                    }
                    1 -> {
                        templatePropertiesData.smallTitleData!!.textColor = color
                        binding.smallEditText.setTextColor(color)
                    }
                    2 -> {
                        templatePropertiesData.lineData!!.lineColor = color
                        binding.barColor.setTextColor(color)
                    }
                    3 -> {
                        templatePropertiesData.backgroundColor = color
                        binding.changeBackgroundButton.setTextColor(color)
                        playBackground()
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
            templatePropertiesData.videoResolution = selectedResolution.substring(0, selectedResolution.length - 1).toInt()
        }
        else if(parent == binding.backgroundTypeSpinner) {
            var selectedType = parent.getItemAtPosition(position).toString()
            if(selectedType.isEmpty()) selectedType = "video"
            templatePropertiesData.backgroundType = selectedType
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    companion object {
        const val TAG = "MainActivity"

        const val CODE_MEDIA_SEARCH = 1110
        const val CODE_ENCODING_FINISHED = 1112
        const val PROGRESS_RESULT = 1990
        const val BACKGROUND_PROGRESS_RESULT = 1991
        const val OUT_FILE_NAME = "out.mp4"
        const val FINAL_VIDEO_NAME = "final.mp4"
        const val CODE_THUMB = 6661
        const val USER_INPUT_DATA = "user-input-data"
        val SELECTED_TEMPLATE = "selected-template"
        const val IMAGE_SEARCH = 11
        const val VIDEO_SEARCH = 21
    }


}