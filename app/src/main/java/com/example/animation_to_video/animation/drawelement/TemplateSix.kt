package com.example.animation_to_video.animation.drawelement

import android.content.Context
import android.graphics.*
import com.example.animation_to_video.userinputtemplate.UserInputForTemplate
import kotlin.math.min
import kotlin.math.pow

/**
 * @author Md Jahirul Islam Hridoy
 * Created on 23,March,2022
 */
class TemplateSix(private val width: Int, private val height: Int,
                  private val context: Context, userInputForTemplate: UserInputForTemplate
) {
    private var textBitmapBig: Bitmap
    private var textBitmapSmall: Bitmap
    private val bitmapFromText = BitmapFromText() // TODO: 3/21/21 hard coded
    private val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        textBitmapBig = bitmapFromText.getBitmap(
            (1f/3f*width).toInt(),500,userInputForTemplate.bigText,
            true,userInputForTemplate.bigTextColor, Color.TRANSPARENT,false)
        textBitmapSmall = bitmapFromText.getBitmap(
            (1f/3f*width).toInt(),500,userInputForTemplate.smallText,
            true,userInputForTemplate.smallTextColor, Color.TRANSPARENT,false
        )

        linePaint.color = userInputForTemplate.barColor
        linePaint.style = Paint.Style.STROKE
        linePaint.isAntiAlias = true
        linePaint.strokeWidth = (1f/50f)*height
        linePaint.strokeJoin = Paint.Join.ROUND
    }

    fun getBitmap(pTime: Long): Bitmap?{
        val bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        var centerX = (1f/2f)*width
        var centerY = (1f/2f)*height

        var textTotalTime = 1000000f
        var textPresTime = min(textTotalTime,(pTime).toFloat())
        var ratio = (textPresTime/textTotalTime).pow(2+1)

        var len = (1f/2f)*height
        var left = centerX
        var top = (1f/2f)*len
        var right = centerX
        var bottom = ratio*len + top

        if(pTime < 1000000L) {
            canvas.rotate(ratio*360f,centerX,centerY)
        }

        canvas.drawLine(left,top,right,bottom,linePaint)

        if(pTime < 1500000L) return bitmap

        textTotalTime = 500000f
        textPresTime = min(textTotalTime,(pTime-1500000L).toFloat())
        ratio = (textPresTime/textTotalTime).pow(4)

        left = 0f
        top = 0f
        right = ratio*textBitmapBig.width
        bottom = textBitmapBig.height.toFloat()

        var subRect = Rect(left.toInt(),top.toInt(),right.toInt(),bottom.toInt())

        left = centerX - 4f*linePaint.strokeWidth - right
        top = centerY - textBitmapBig.height / 2f
        right = centerX - 4f*linePaint.strokeWidth
        bottom = centerY + textBitmapBig.height / 2f

        var desRect = Rect(left.toInt(),top.toInt(),right.toInt(),bottom.toInt())

        canvas.drawBitmap(textBitmapBig,subRect,desRect,bitmapPaint)

        if(pTime < 2000000L) return bitmap

        textTotalTime = 500000f
        textPresTime = min(textTotalTime,(pTime-2000000L).toFloat())
        ratio = (textPresTime/textTotalTime).pow(4)

        left = 0f
        top = 0f
        right = ratio*textBitmapSmall.width
        bottom = textBitmapSmall.height.toFloat()

        subRect = Rect(left.toInt(),top.toInt(),right.toInt(),bottom.toInt())

        left = centerX + 4f*linePaint.strokeWidth
        top = centerY - textBitmapSmall.height / 2f
        right = left + right
        bottom = centerY + textBitmapSmall.height / 2f

        desRect = Rect(left.toInt(),top.toInt(),right.toInt(),bottom.toInt())

        canvas.drawBitmap(textBitmapSmall,subRect,desRect,bitmapPaint)

        return bitmap
    }
}