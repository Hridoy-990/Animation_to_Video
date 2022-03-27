package com.example.animation_to_video.animation.drawelement

import android.content.Context
import android.graphics.*
import com.example.animation_to_video.userinputtemplate.UserInputForTemplate
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * @author Md Jahirul Islam Hridoy
 * Created on 23,March,2022
 */
class TemplateFour(private val width: Int, private val height: Int,
                   private val context: Context, userInputForTemplate: UserInputForTemplate
) {
    private var textBitmapBig: Bitmap
    private var textBitmapSmall: Bitmap
    private val bitmapFromText = BitmapFromText()
    private val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rectPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        textBitmapBig = bitmapFromText.getBitmap(
            1000,((1f/2f)*height).toInt(), userInputForTemplate.bigText,
            false,userInputForTemplate.bigTextColor, Color.TRANSPARENT,true)

        textBitmapSmall = bitmapFromText.getBitmap(1000,((1f/2f)*height).toInt()
            ,userInputForTemplate.smallText,false,
            userInputForTemplate.smallTextColor, Color.TRANSPARENT,true)

        bitmapPaint.color = Color.RED

        linePaint.color = userInputForTemplate.barColor
        linePaint.style = Paint.Style.STROKE
        linePaint.isAntiAlias = true
        linePaint.strokeWidth = (1f/20f)*height
        linePaint.strokeJoin = Paint.Join.ROUND

        rectPaint.color = Color.WHITE
        rectPaint.style = Paint.Style.STROKE
        rectPaint.isAntiAlias = false
        rectPaint.strokeWidth = (1f/20f)*height

    }

    fun getBitmap(pTime: Long): Bitmap?{
        val bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val centerX = (1f/2f)*width
        val centerY = (1f/2f)*height + rectPaint.strokeWidth
        val left = (1f/6f)*width
        val top = (1f/5f)*height
        val right = (5f/6f)*width
        val bottom = (4f/5f)*height
        val lenX = (2f/6f)*width
        var lenY = (3f/10f)*height

        var t = 1000000f
        var p = min(t,pTime.toFloat())

        canvas.drawRoundRect(centerX - ((p/t).pow(5))*lenX,top,centerX + ((p/t).pow(5))*lenX,bottom,10f,10f,rectPaint)

        if(pTime < 4000000L) return bitmap

        canvas.drawRoundRect(left,top,right,bottom,10f,10f,rectPaint)

        t = 500000f
        p = min(t,(pTime - 4000000L).toFloat())

        lenY -= rectPaint.strokeWidth
        var desRect = Rect(
            (centerX - lenX + 3f*rectPaint.strokeWidth).toInt(),
            (centerY - p/t*lenY).toInt(),
            (centerX + lenX - 3f*rectPaint.strokeWidth).toInt(),
            (centerY).toInt()
        )

        var subRect = Rect(0,0,textBitmapBig.width,(p/t*textBitmapBig.height).toInt() )

        canvas.drawBitmap(textBitmapBig,subRect,desRect,bitmapPaint)

        if(pTime < 4600000L) return bitmap

        t = 500000f
        p = min(t,(pTime-4600000L).toFloat())

        desRect = Rect(
            (centerX - lenX + 6f*rectPaint.strokeWidth).toInt(),
            (centerY).toInt(),
            (centerX + lenX - 6f*rectPaint.strokeWidth).toInt(),
            max(centerY,(centerY + p/t*lenY) - 3f*rectPaint.strokeWidth).toInt()
        )

        subRect = Rect(0,0,textBitmapSmall.width,(p/t*textBitmapSmall.height).toInt())

        canvas.drawBitmap(textBitmapSmall,subRect,desRect,rectPaint)

        return bitmap
    }
}