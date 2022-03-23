package com.example.animation_to_video.animation.drawelement

import android.content.Context
import android.graphics.*
import android.util.Log
import com.example.animation_to_video.userinputtemplate.UserInputForTemplate
import kotlin.math.min

/**
 * @author Md Jahirul Islam Hridoy
 * Created on 22,March,2022
 */
class TemplateThree(
    private val width: Int, private val height: Int,
    private val context: Context, userInputForTemplate: UserInputForTemplate
) {
    private var textBitmapBig: Bitmap
    private var textBitmapSmall: Bitmap
    private var textBitmapExtraSmall : Bitmap
    private val bitmapFromText = BitmapFromText()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    init {

        textBitmapBig = bitmapFromText.getBitmap(1000,((1f/5f)*height).toInt(), userInputForTemplate.bigText, false,userInputForTemplate.bigTextColor,Color.TRANSPARENT,true)
        textBitmapSmall = bitmapFromText.getBitmap(1000,((1f/10f)*height).toInt(),
            userInputForTemplate.smallText,false,
            userInputForTemplate.smallTextColor,Color.TRANSPARENT,true)
        textBitmapExtraSmall = bitmapFromText.getBitmap(1000 ,((1f/15f)*height).toInt(), userInputForTemplate.smallText,false,
            userInputForTemplate.smallTextColor,Color.TRANSPARENT,true)
        linePaint.color = Color.RED
        linePaint.style = Paint.Style.STROKE
        linePaint.isAntiAlias = true
        linePaint.strokeWidth = (1f / 20f) * height
        linePaint.strokeJoin = Paint.Join.ROUND
    }

    fun getBitmap(pTime: Long): Bitmap?{
        val bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val xOffset = (1f/10f)*width
        val yOffset = height/3f

        Log.e(TAG, "getBitmap: pTime -> $pTime", )
         // Draw Left line
        if(pTime > 1000000L){
            // start draw line
            val lineTotalTime = 2000000f
            val linePresTime = min(lineTotalTime,(pTime-1000000L).toFloat())

            val lineStartX = xOffset
            val lineStartY = yOffset
            val lineEndX = xOffset
            val lineEndY = yOffset + (height/2f)*(linePresTime/lineTotalTime)

            canvas.drawLine(lineStartX,lineStartY,lineEndX,lineEndY,linePaint)

            // end draw line
        }

        // draw big text
        if(pTime > 2500000L) {
            val textTotalTime = 2000000f
            val textPresTime = min(textTotalTime,(pTime-2500000L).toFloat())

            val subRect = Rect(0,0,(textPresTime/textTotalTime * textBitmapBig.width).toInt(), textBitmapBig.height)
            val desRect = Rect(
                xOffset.toInt() + linePaint.strokeWidth.toInt(),
                yOffset.toInt(),
                (xOffset+textBitmapBig.width + linePaint.strokeWidth).toInt(),
                (yOffset+ height/6f).toInt()
            )
            canvas.drawBitmap(textBitmapBig, subRect, desRect, paint)
        }

        // draw small text
        if(pTime > 5500000L){
            val textTotalTime = 1000000f
            val textPresTime = min(textTotalTime,(pTime-5500000L).toFloat())

            val subRect = Rect(0,0,(textPresTime/textTotalTime * textBitmapSmall.width).toInt(), textBitmapSmall.height)
            val desRect = Rect(
                xOffset.toInt() + linePaint.strokeWidth.toInt(),
                (yOffset+ height/6f).toInt(),
                (xOffset + linePaint.strokeWidth + textPresTime/textTotalTime *(textBitmapSmall.width)).toInt(),
                (yOffset+ height/3f).toInt()
            )
            canvas.drawBitmap(textBitmapSmall, subRect, desRect, paint)
        }

        // draw Extra small text

        if(pTime > 6500000L){
            val textTotalTime = 800000f
            val textPresTime = min(textTotalTime,(pTime-6500000L).toFloat())

            val subRect = Rect(0,0,(textPresTime/textTotalTime * textBitmapExtraSmall.width).toInt(), textBitmapExtraSmall.height)
            val desRect = Rect(
                xOffset.toInt() + linePaint.strokeWidth.toInt(),
                (yOffset + height/3f).toInt(),
                (xOffset + textBitmapExtraSmall.width + linePaint.strokeWidth).toInt(),
                (yOffset + height/2f).toInt()
            )
            canvas.drawBitmap(textBitmapExtraSmall , subRect, desRect, paint)
        }

        // Right line draw
        if(pTime > 2500000L){
            // start draw line
            val lineTotalTime = 2000000f
            val linePresTime = min(lineTotalTime,(pTime-2500000L).toFloat())

            val lineStartX = xOffset + width / 1.5f
            val lineStartY = yOffset
            val lineEndX = xOffset + width / 1.5f
            val lineEndY = yOffset + (height/2f)*(linePresTime/lineTotalTime)

            canvas.drawLine(lineStartX,lineStartY,lineEndX,lineEndY,linePaint)

            // end draw line
        }

        return bitmap
    }
    
    companion object {
        private const val TAG = "TemplateThree"
    }
}