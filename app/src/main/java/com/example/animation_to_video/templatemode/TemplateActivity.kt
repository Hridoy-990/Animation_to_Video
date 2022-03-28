package com.example.animation_to_video.templatemode

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import android.widget.VideoView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.animation_to_video.*
import com.example.animation_to_video.databinding.ActivityTemplateBinding
import com.example.animation_to_video.editor.EditorActivity
import com.example.animation_to_video.repo.TemplatePreviewRepo
import com.example.animation_to_video.repo.TemplateRepository

class TemplateActivity : AppCompatActivity() , HorizontalAdapter.OnItemClickListener {

    private lateinit var binding : ActivityTemplateBinding

    private lateinit var templateAdapter: TemplateAdapter
    private val templatePreviewRepo = TemplatePreviewRepo()
    private val verticalList = templatePreviewRepo.getCategoryList()
    private val verticalAdapter = VerticalAdapter(this,verticalList,this)
    private val templateRepository = TemplateRepository()
    private val templateData = TemplateData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTemplateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getPermission()

        binding.verticalRecyclerview.setHasFixedSize(true)
        binding.verticalRecyclerview.layoutManager = LinearLayoutManager(this , LinearLayoutManager.VERTICAL , false)
        binding.verticalRecyclerview.adapter = verticalAdapter

    }


    private fun getPermission() {
        if(needsPermission(this)) {
            requestPermission(this,0)
        }
    }


    override fun onItemClick(type: String, position: Int) {
        if(type == AppConstants.EXPAND) {
            Toast.makeText(this,
                "expand ${templatePreviewRepo.getCategoryNameBySerial(position)} coming soon"
                ,Toast.LENGTH_SHORT).show()
        }
        else if(type == AppConstants.TEMPLATE) {
            previewDialogForConfirmation(position)
/*            val intent = Intent(this,EditorActivity::class.java)
            intent.putExtra(AppConstants.SELECTED_TEMPLATE,position)
            startActivity(intent)
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right)*/
        }
    }

    fun previewDialogForConfirmation(selectedTemplate: Int) {
        val previewDialog = Dialog(this)
        previewDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        previewDialog.setContentView(R.layout.preview_dialog)
        val lp = WindowManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lp.copyFrom(previewDialog.window!!.attributes)
        previewDialog.window!!.attributes = lp
        previewDialog.window!!.attributes.windowAnimations = R.style.DialogFadeAnimation
        val previewVideo = previewDialog.findViewById<VideoView>(R.id.vv_preview_dialog)
        previewVideo.setVideoURI(
            Uri.parse(
                templateRepository
                    .getTemplateData(selectedTemplate)
                    .templatePreviewVideoUri
            )
        )
        previewVideo.start()
        previewDialog.show()
        previewDialog
            .findViewById<Button>(R.id.bt_cancel_preview_dialog)
            .setOnClickListener {
                previewDialog.dismiss()
            }
        previewDialog
            .findViewById<Button>(R.id.bt_use_preview_dialog)
            .setOnClickListener {
                val intent = Intent(this, EditorActivity::class.java)
                intent.putExtra(AppConstants.SELECTED_TEMPLATE,selectedTemplate)
                startActivity(intent)
                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right)
                previewDialog.dismiss()
            }
    }


    companion object {
        const val SELECTED_TEMPLATE = "selected-template"
        const val TEMPLATE_DATA = "template-data"
    }


}