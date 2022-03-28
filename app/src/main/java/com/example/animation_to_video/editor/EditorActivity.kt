package com.example.animation_to_video.editor

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Toast
import com.example.animation_to_video.AppConstants
import com.example.animation_to_video.MainActivity
import com.example.animation_to_video.R
import com.example.animation_to_video.databinding.ActivityEditorBinding
import com.example.animation_to_video.datamodel.TemplatePropertiesData
import com.example.animation_to_video.repo.TemplateRepository
import yuku.ambilwarna.AmbilWarnaDialog

class EditorActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var binding : ActivityEditorBinding

    private var selectedTemplate: Int = 0

    private val templateRepository = TemplateRepository()
    lateinit var templatePropertiesData: TemplatePropertiesData

    private lateinit var mToast: Toast
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mScrollView.smoothScrollTo(0, 0)
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT)
        selectedTemplate = intent.getIntExtra(AppConstants.SELECTED_TEMPLATE, 0)

        templatePropertiesData = templateRepository.getTemplateData(selectedTemplate)

        templatePropertiesData.backgroundVideoUri = templateRepository.getTemplateBackgroundVideoUri(
            this,
            selectedTemplate
        )

        templatePropertiesData.backgroundImageUri = templateRepository.getTemplateBackgroundImageUri(
            selectedTemplate
        )

        initializeView()
    }

    private fun initializeView() {
        initializeViewsFromTemplate()
        setListeners()
        setSpinners()
        binding.rlBigtitleExpansion.visibility = View.GONE
        binding.rlSmalltitleExpansion.visibility = View.GONE
        binding.rlBackgroundExpand.visibility = View.GONE
        binding.rlBarelementExpand.visibility = View.GONE
    }

    private fun setSpinners() {

        // spinner for resolution --------------------------------------------------
        val resolutionSpinnerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.resolution_values,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spVideoResolution.adapter = adapter
        }

        val defaultResolutionPosition = resolutionSpinnerAdapter.getPosition("720p")
        binding.spVideoResolution.setSelection(defaultResolutionPosition)
        templatePropertiesData.videoResolution = 720
        binding.spVideoResolution.onItemSelectedListener = this

        // ----------------------------------------------------------

        // spinner background type --------------------------------------

        val backgroundTypeSpinnerAdapter = ArrayAdapter.createFromResource(
            this, R.array.background_types, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spBackgroundType.adapter = adapter
        }

        val defaultBackgroundTypePosition = backgroundTypeSpinnerAdapter.getPosition(
            templatePropertiesData.backgroundType
        )
        binding.spBackgroundType.setSelection((defaultBackgroundTypePosition))
        binding.spBackgroundType.onItemSelectedListener = this

        // ------------------------------------------------------------------------

    }

    private fun initializeViewsFromTemplate() {

        if(templatePropertiesData.bigTitleData != null){
            binding.etBigtext.setText(templatePropertiesData.bigTitleData!!.text)
            binding.etBigtext.setTextColor(templatePropertiesData.bigTitleData!!.textColor)
        }
        else binding.cvBigtext.visibility = View.GONE

        if(templatePropertiesData.smallTitleData != null) {
            binding.etSmalltext.setText(templatePropertiesData.smallTitleData!!.text)
            binding.etSmalltext.setTextColor(templatePropertiesData.smallTitleData!!.textColor)
        }
        else binding.cvSmalltext.visibility = View.GONE

        when {
            templatePropertiesData.lineData != null -> {
                binding.ivBarElement.setImageURI(null)
                binding.ivBarElement.setBackgroundColor(templatePropertiesData.lineData!!.lineColor)
            }
            templatePropertiesData.rectangleData != null -> {

            }
            else -> binding.cvBarElement.visibility = View.GONE
        }

/*        bigTitleEditText.setText(templateDataSingle.bigText)
        bigTitleEditText.setTextColor(templateDataSingle.bigTextColor)
        smallTextEditText.setText(templateDataSingle.smallText)
        smallTextEditText.setTextColor(templateDataSingle.smallTextColor)
        barElementImageView.setImageURI(null)
        barElementImageView.setBackgroundColor(templateDataSingle.barColor)*/
        initializeBackgroundPreview()
        binding.vvTemplatePreview.setVideoURI(Uri.parse(templatePropertiesData.templatePreviewVideoUri))
        binding.vvTemplatePreview.seekTo(1)
    }

    private fun initializeBackgroundPreview() {
        when(templatePropertiesData.backgroundType) {
            AppConstants.BackgroundType.VIDEO -> {
                binding.vvBackgroundPreview.setVideoURI(
                    Uri.parse(templatePropertiesData.backgroundVideoUri)
                )
                binding.vvBackgroundPreview.seekTo(1)
                binding.ivBackgroundPreview.visibility = View.GONE
                binding.vvBackgroundPreview.visibility = View.VISIBLE
                binding.ibtPlayBackgroundVideo.visibility = View.VISIBLE
            }
            AppConstants.BackgroundType.IMAGE -> {
                binding.ivBackgroundPreview.setImageURI(
                    Uri.parse(templatePropertiesData.backgroundImageUri)
                )
                binding.ivBackgroundPreview.setBackgroundColor(Color.TRANSPARENT)
                binding.vvBackgroundPreview.visibility = View.GONE
                binding.ibtPlayBackgroundVideo.visibility = View.GONE
                binding.ivBackgroundPreview.visibility = View.VISIBLE
            }
            AppConstants.BackgroundType.COLOR -> {
                binding.ivBackgroundPreview.setImageResource(0)
                binding.ivBackgroundPreview.setBackgroundColor(templatePropertiesData.backgroundColor)
                binding.vvBackgroundPreview.visibility = View.GONE
                binding.ibtPlayBackgroundVideo.visibility = View.GONE
                binding.ivBackgroundPreview.visibility = View.VISIBLE
            }
        }
    }

    fun showToast(message: String) {
        mToast.cancel()
        mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        mToast.show()
    }

    private fun setListeners() {
        binding.ibtBigtextColorPicker.setOnClickListener {
            colorPicker(0) // 0 for big text color
        }
        binding.ibtSmalltextColorPicker.setOnClickListener {
            colorPicker(1) // 1 for small text color
        }
        binding.ibtBarElementColorPicker.setOnClickListener {
            colorPicker(2) // 2 for bar element
        }

        binding.btChangeBackground.setOnClickListener {
            when(templatePropertiesData.backgroundType) {
                AppConstants.BackgroundType.VIDEO -> {
                    val gallery = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    )
                    startActivityForResult(gallery, MainActivity.VIDEO_SEARCH)
                }
                AppConstants.BackgroundType.IMAGE -> {
                    val gallery = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    startActivityForResult(gallery, MainActivity.IMAGE_SEARCH)
                }
                AppConstants.BackgroundType.COLOR -> colorPicker(3)
            }
        }


        binding.ibtPlayBackgroundVideo.setOnClickListener {
            binding.ibtPlayBackgroundVideo.visibility = View.GONE
            binding.vvBackgroundPreview.start()
            binding.vvBackgroundPreview.setOnCompletionListener {
                binding.ibtPlayBackgroundVideo.visibility = View.VISIBLE
            }
        }

        binding.btPlayTemplatePreview.setOnClickListener {

            Log.e(TAG, "setListeners: ${templatePropertiesData.templatePreviewVideoUri}", )
            // redundant work ----------------
            binding.vvTemplatePreview.setVideoURI(Uri.parse(templatePropertiesData.templatePreviewVideoUri))
            binding.vvTemplatePreview.seekTo(1)
            // -----------------------------
            binding.vvTemplatePreview.start()
        }

        binding.btExport.setOnClickListener {
            finalizeUserInput() // update template properties for the last time
            val intent = Intent(this, ProcessingActivity::class.java)
            intent.putExtra(AppConstants.FINAL_TEMPLATE_DATA, templatePropertiesData)
            startActivity(intent)
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        binding.ibtBigtextPositionChange.setOnClickListener { showToast("Coming Soon!") }
        binding.ibtBigtextFontChange.setOnClickListener { showToast("Coming Soon!") }
        binding.ibtSmalltextPositionChange.setOnClickListener { showToast("Coming Soon!") }
        binding.ibtSmalltextFontChange.setOnClickListener { showToast("Coming Soon!") }
        binding.ibtBarElementPosition.setOnClickListener { showToast("Coming Soon!") }

        binding.ibtExpandBigtitile.setOnClickListener {
            binding.ibtExpandBigtitile.isEnabled = false
            if(binding.rlBigtitleExpansion.visibility == View.VISIBLE) {
                collapseView(binding.ibtExpandBigtitile,binding.rlBigtitleExpansion)
            }
            else {
                expandView(binding.ibtExpandBigtitile,binding.rlBigtitleExpansion)
            }
        }
        binding.ibtExpandSmalltitle.setOnClickListener {
            binding.ibtExpandSmalltitle.isEnabled = false
            if(binding.rlSmalltitleExpansion.visibility == View.VISIBLE) {
                collapseView(binding.ibtExpandSmalltitle,binding.rlSmalltitleExpansion)
            }
            else {
                expandView(binding.ibtExpandSmalltitle,binding.rlSmalltitleExpansion)
            }
        }

        binding.ibtExpandBarelement.setOnClickListener {
            binding.ibtExpandBarelement.isEnabled = false
            if(binding.rlBarelementExpand.visibility == View.VISIBLE) {
                collapseView(binding.ibtExpandBarelement,binding.rlBarelementExpand)
            }
            else {
                expandView(binding.ibtExpandBarelement,binding.rlBarelementExpand)
            }
        }
        binding.ibtExpandBackgroundPreview.setOnClickListener {
            binding.ibtExpandBackgroundPreview.isEnabled = false
            if(binding.rlBackgroundExpand.visibility == View.VISIBLE) {
                collapseView(binding.ibtExpandBackgroundPreview,binding.rlBackgroundExpand)
            }
            else {
                expandView(binding.ibtExpandBackgroundPreview,binding.rlBackgroundExpand)
            }
        }
    }

    private fun collapseView(triggerButton : ImageButton, view: View) {
        triggerButton.animate()
            .rotationBy(180f)
            .setDuration(700)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    triggerButton.isEnabled = true
                }
            })
            .start()

        view.animate()
            .alpha(0.0f)
            .setDuration(700)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    view.visibility = View.GONE
                }
            })
            .start()
    }

    private fun expandView(triggerButton: ImageButton, view: View) {
        triggerButton.animate()
            .rotationBy(-180f)
            .setDuration(700)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    triggerButton.isEnabled = true
                }
            })
            .start()

        view.animate()
            .alpha(1.0f)
            .setDuration(700)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    super.onAnimationStart(animation)
                    view.visibility = View.VISIBLE

                }
            })
            .start()
    }

    fun colorPicker(id: Int) {

        val initialColor = when(id) {
            0 -> templatePropertiesData.bigTitleData!!.textColor
            1 -> templatePropertiesData.smallTitleData!!.textColor
            2 -> templatePropertiesData.lineData!!.lineColor
            3 -> templatePropertiesData.backgroundColor
            else -> 0
        }

        val dialog = AmbilWarnaDialog(
            this,
            initialColor,
            object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onOk(dialog: AmbilWarnaDialog, color: Int) {
                    when (id) {
                        0 -> {
                            templatePropertiesData.bigTitleData!!.textColor = color
                            binding.etBigtext.setTextColor(color)
                        }
                        1 -> {
                            templatePropertiesData.smallTitleData!!.textColor = color
                            binding.etSmalltext.setTextColor(color)
                        }
                        2 -> {
                            templatePropertiesData.lineData!!.lineColor = color
                            binding.ivBarElement.setBackgroundColor(templatePropertiesData.lineData!!.lineColor)
                        }
                        3 -> {
                            templatePropertiesData.backgroundColor = color
                            initializeBackgroundPreview()
                        }
                    }
                }

                override fun onCancel(dialog: AmbilWarnaDialog) {}
            })

        dialog.show()
    }

    fun finalizeUserInput() {
        if(templatePropertiesData.bigTitleData != null) {
            var text = binding.etBigtext.text.toString()
            if(text.isEmpty()) text = " "
            templatePropertiesData.bigTitleData!!.text = text
        }
        if(templatePropertiesData.smallTitleData != null) {
            var text = binding.etSmalltext.text.toString()
            if(text.isEmpty()) text = " "
            templatePropertiesData.smallTitleData!!.text = text
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == MainActivity.IMAGE_SEARCH) {
            templatePropertiesData.backgroundImageUri = (data?.data).toString()
            initializeBackgroundPreview()
        }
        else if(resultCode == RESULT_OK && requestCode == MainActivity.VIDEO_SEARCH) {
            val uri = (data?.data).toString()
            val mp = MediaPlayer.create(this,Uri.parse(uri))
            if(mp.duration > 60000){
                showToast("Please Select Video with duratin < 1 min!")
            }
            else {
                templatePropertiesData.backgroundVideoUri = uri
                initializeBackgroundPreview()
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if(parent == binding.spVideoResolution) {
            var selectedResolution = parent.getItemAtPosition(position).toString()
            if(selectedResolution.isEmpty()) selectedResolution = "720p"
            templatePropertiesData.videoResolution = selectedResolution.substring(
                0,
                selectedResolution.length - 1
            ).toInt()
        }
        else if(parent == binding.spBackgroundType) {
            var selectedType = parent.getItemAtPosition(position).toString()
            if(selectedType.isEmpty()) selectedType = AppConstants.BackgroundType.VIDEO
            templatePropertiesData.backgroundType = selectedType
            initializeBackgroundPreview()
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) { }

    companion object {
        const val TAG = "EditorActivity"
    }
}