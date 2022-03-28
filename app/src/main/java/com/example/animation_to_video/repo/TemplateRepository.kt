package com.example.animation_to_video.repo

import android.content.Context
import android.graphics.Color
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.animation_to_video.AppConstants
import com.example.animation_to_video.R
import com.example.animation_to_video.datamodel.LinePropertiesData
import com.example.animation_to_video.datamodel.TemplatePropertiesData
import com.example.animation_to_video.datamodel.TextPropertiesData
import java.io.File

/**
 * @author Md Jahirul Islam Hridoy
 * Created on 27,March,2022
 */
class TemplateRepository {
    private val templatePropertiesDataArray: ArrayList<TemplatePropertiesData> = ArrayList()

    init {
        // template 0
        val lineData3 = LinePropertiesData(
            Color.YELLOW,"stroke",(1f/50f),(3f/11f),(1f/2f),(8f/11f),(1f/2f),
            0L,500000L, AppConstants.TransitionType.SMALL_TO_LARGE,
            AppConstants.RotationType.NO_ROTATION, 4f,true
        )
        val bigTitleData3 = TextPropertiesData(
            "Lii Lab","font", Color.RED, Color.argb(50,255,255,255),"style",
            (4f/11f),(1.5f/8f),(7f/11f),(3.5f/8f),500000L,1500000L,
            AppConstants.TransitionType.BOTTOM_TO_TOP, AppConstants.RotationType.NO_ROTATION,
            2f,true
        )
        val smallTitleData3 = TextPropertiesData(
            "Live in imagination","font", Color.WHITE, Color.TRANSPARENT,"style",
            (4f/11f),(4.5f/8f),(7f/11f),(5.5f/8f),1200000L,2000000L,
            AppConstants.TransitionType.TOP_TO_BOTTOM, AppConstants.RotationType.NO_ROTATION,
            5f,false
        )
        templatePropertiesDataArray.add(
            TemplatePropertiesData(
                bigTitleData3,smallTitleData3,lineData3,null,null,
                AppConstants.BackgroundType.VIDEO, Color.WHITE,"default",
                "default",1.0f,720,0,
                R.drawable.previewimage3,"android.resource://" + "com.example.animation_to_video" + "/" + R.raw.videopreview3
            )
        )

        // template 1
        val lineData1 = LinePropertiesData(
            Color.YELLOW,"stroke",(1f/40f),(1f/6f),(1f/4f),(1f/6f),(3f/4f),
            1000000L,1500000L, AppConstants.TransitionType.SMALL_TO_LARGE,
            AppConstants.RotationType.CLOCKWISE, 2f,true
        )
        val bigTitleData1 = TextPropertiesData(
            "Lii Lab","font", Color.RED, Color.TRANSPARENT,"style",
            (1.2f/6f),(2f/7f),(3.2f/6f),(3.5f/7f),1600000L,2600000L,
            AppConstants.TransitionType.LEFT_TO_RIGHT, AppConstants.RotationType.NO_ROTATION,
            4f,true
        )
        val smallTitleData1 = TextPropertiesData(
            "Live in imagination","font", Color.WHITE, Color.TRANSPARENT,"style",
            (1.2f/6f),(4f/7f),(2.5f/6f),(5f/7f),2500000L,3000000L,
            AppConstants.TransitionType.RIGHT_TO_LEFT, AppConstants.RotationType.NO_ROTATION,
            2f,false
        )
        templatePropertiesDataArray.add(
            TemplatePropertiesData(
                bigTitleData1,smallTitleData1,lineData1,null,null,
                AppConstants.BackgroundType.VIDEO, Color.WHITE,"default",
                "default",1.0f,720,1,
                R.drawable.previewimage1,"android.resource://" + "com.example.animation_to_video" + "/" + R.raw.videopreview1
            )
        )

        // template 2
        val bigTitleData2 = TextPropertiesData(
            "Lii Lab","font", Color.argb(200,81, 97, 125), Color.TRANSPARENT,"style",
            (1f/5f),(1f/3f),(4f/5f),(2f/3f),200000L,2000000L,
            AppConstants.TransitionType.SMALL_TO_LARGE, AppConstants.RotationType.CLOCKWISE,
            2f,true
        )
        templatePropertiesDataArray.add(
            TemplatePropertiesData(
                bigTitleData2,null,null,null,null,
                AppConstants.BackgroundType.VIDEO, Color.WHITE,"default",
                "default",1.0f,720,2,
                R.drawable.previewimage2,"android.resource://" + "com.example.animation_to_video" + "/" + R.raw.videopreview2
            )
        )

        // template 3
        val bigTitleData0 = TextPropertiesData(
            "Lii Lab","font", Color.RED, Color.TRANSPARENT,"style",
            (1f/3f),(1f/3f),(2f/3f),(2f/3f),1000000L,2000000L,
            AppConstants.TransitionType.SMALL_TO_LARGE, AppConstants.RotationType.NO_ROTATION,
            2f+1f,true
        )
        templatePropertiesDataArray.add(
            TemplatePropertiesData(
                bigTitleData0,null,null,null,null,
                AppConstants.BackgroundType.COLOR, Color.WHITE,"default",
                "default",1.0f,720,2+1,
                R.drawable.previewimage0,"android.resource://" + "com.example.animation_to_video" + "/" + R.raw.videopreview0
            )
        )

        // template 4
        val lineData4 = LinePropertiesData(
            Color.WHITE,"stroke",(1f/50f),(1f/2f),(1f/5f),(1f/2f),(4f/5f),
            0L,1000000L, AppConstants.TransitionType.TOP_TO_BOTTOM,
            AppConstants.RotationType.NO_ROTATION, 1f/2f,true
        )
        val bigTitleData4 = TextPropertiesData(
            "Lii Lab","font", Color.RED, Color.argb(50,255,255,255),"style",
            (3f/16f),(2f/5f),(7f/16f),(3f/5f),900000L,2000000L,
            AppConstants.TransitionType.BOTTOM_TO_TOP, AppConstants.RotationType.NO_ROTATION,
            2f,true
        )
        val smallTitleData4 = TextPropertiesData(
            "Live in imagination","font", Color.WHITE, Color.argb(50,255,255,255),"style",
            (9f/16f),(2.2f/5f),(13f/16f),(2.8f/5f),1800000L,2400000L,
            AppConstants.TransitionType.TOP_TO_BOTTOM, AppConstants.RotationType.NO_ROTATION,
            5f,false
        )
        templatePropertiesDataArray.add(
            TemplatePropertiesData(
                bigTitleData4,smallTitleData4,lineData4,null,null,
                AppConstants.BackgroundType.COLOR, Color.argb(255,149, 164, 191),"default",
                "default",1.0f,720,4,
                R.drawable.previewimage4,"android.resource://" + "com.example.animation_to_video" + "/" + R.raw.videopreview4
            )
        )
    }

    fun getTemplateData(selectedTemplate: Int) : TemplatePropertiesData {
        return templatePropertiesDataArray[selectedTemplate%getTotalTemplateCount()]
    }

    fun getTotalTemplateCount() : Int {
        return  5
    }

    fun getTemplateBackgroundVideoUri(context: Context, selectedTemplate: Int): String {

        return when(selectedTemplate%getTotalTemplateCount()) {
            0 -> FileProvider.getUriForFile(context, "com.example.animation_to_video.provider", File(context.cacheDir.absolutePath + "/tmpbg0.mp4")).toString()
            1 -> FileProvider.getUriForFile(context, "com.example.animation_to_video.provider", File(context.cacheDir.absolutePath + "/tmpbg1.mp4")).toString()
            2 -> FileProvider.getUriForFile(context, "com.example.animation_to_video.provider", File(context.cacheDir.absolutePath + "/tmpbg2.mp4")).toString()
            2+1 -> FileProvider.getUriForFile(context, "com.example.animation_to_video.provider", File(context.cacheDir.absolutePath + "/tmpbg3.mp4")).toString()
            4 -> FileProvider.getUriForFile(context, "com.example.animation_to_video.provider", File(context.cacheDir.absolutePath + "/tmpbg4.mp4")).toString()
            else -> FileProvider.getUriForFile(context, "com.example.animation_to_video.provider", File(context.cacheDir.absolutePath + "/tmpbg0.mp4")).toString()
        }

    }

    fun getTemplateBackgroundImageUri(selectedTemplate: Int) : String {
        return (Uri.parse("android.resource://com.example.animation_to_video/drawable/bac4")).toString()
    }

}