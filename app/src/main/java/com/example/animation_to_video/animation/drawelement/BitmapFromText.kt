package com.example.animation_to_video.animation.drawelement

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

/**
 * @author Md Jahirul Islam Hridoy
 * Created on 22,March,2022
 */
class BitmapFromText {

    private val textPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    fun getBitmap(width : Int , height : Int , text : String , widthPriority: Boolean, color: String) : Bitmap {
        textPaint.color = Color.parseColor(color)
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 200f

        val baseline = -textPaint.ascent() // ascent() is negative
        val textWidth = (textPaint.measureText(text) + 0.5f).toInt() // round
        val textHeight = (baseline + textPaint.descent() + 0.5f).toInt()
        val textBitmap = Bitmap.createBitmap(textWidth, textHeight, Bitmap.Config.ARGB_8888) // text as bitmap
        val textCanvas = Canvas(textBitmap)
        textCanvas.drawText(text, 0f,baseline,textPaint)

        return if(widthPriority) Bitmap.createScaledBitmap(textBitmap, width, ((textBitmap.height.toFloat()/textBitmap.width.toFloat())*width).toInt(), false)
        else Bitmap.createScaledBitmap(textBitmap, ((textBitmap.width.toFloat()/textBitmap.height.toFloat())*height).toInt(), height, false)
    }
}