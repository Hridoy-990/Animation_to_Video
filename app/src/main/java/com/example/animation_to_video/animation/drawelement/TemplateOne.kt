package com.example.animation_to_video.animation.drawelement

import android.content.Context
import android.graphics.*
import com.example.animation_to_video.userinputtemplate.UserInputForTemplate
import kotlin.math.min

/**
 * @author Md Jahirul Islam Hridoy
 * Created on 22,March,2022
 */
class TemplateOne(
    private val width: Int, private val height: Int,
    private val context: Context, userInputForTemplate: UserInputForTemplate
) {
    private var textBitmap: Bitmap
    private val bitmapFromText = BitmapFromText()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        textBitmap = bitmapFromText.getBitmap(
            ((1f+2f)/5f*width).toInt(),100,userInputForTemplate.bigText,
            true,userInputForTemplate.bigTextColor,Color.TRANSPARENT,false)
        paint.color = Color.RED

        linePaint.color = userInputForTemplate.barColor
        linePaint.style = Paint.Style.STROKE
        linePaint.isAntiAlias = true
        linePaint.strokeWidth = (1f / 20f) * height
        linePaint.strokeJoin = Paint.Join.ROUND
    }

    fun gitBitmap(pTime: Long): Bitmap? {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val xOffset = (1f / 5f) * width
        val yOffset = height / 2f - textBitmap.height / 2f

        if(pTime > 1000000L){
            val textTotalTime = 4000000f
            val textPresTime = min(textTotalTime,(pTime-1000000L).toFloat())
            val ratio = textPresTime/textTotalTime

            val subRect = Rect(0,0,(ratio * textBitmap.width).toInt(), textBitmap.height)
            val desRect = Rect(
                (xOffset + textBitmap.width - textPresTime/textTotalTime * textBitmap.width).toInt(),
                yOffset.toInt(),
                (xOffset+textBitmap.width).toInt(),
                (yOffset+textBitmap.height).toInt())
            canvas.drawBitmap(textBitmap, subRect, desRect, paint)
            //canvas.drawBitmap(textBitmap,(1f/(2f+1f)*width),(height/2f)- textBitmap!!.height,paint)


            val lineStartX = xOffset
            val lineStartY = yOffset + textBitmap.height + linePaint.strokeWidth
            val lineEndX = xOffset + ratio * textBitmap.width
            val lineEndY = lineStartY

            canvas.drawLine(lineStartX,lineStartY,lineEndX,lineEndY,linePaint)
            canvas.drawLine(lineStartX, yOffset - linePaint.strokeWidth,lineEndX,yOffset - linePaint.strokeWidth,linePaint)
        }

        return bitmap

    }

}