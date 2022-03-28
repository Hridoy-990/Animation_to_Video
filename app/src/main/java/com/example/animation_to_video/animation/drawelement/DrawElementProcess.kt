package com.example.animation_to_video.animation.drawelement

import android.graphics.*
import com.example.animation_to_video.AppConstants
import com.example.animation_to_video.datamodel.TemplatePropertiesData
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.pow

/**
 * @author Md Jahirul Islam Hridoy
 * Created on 21,March,2022
 */
class DrawElementProcess(val width: Int,val height: Int, val templatePropertiesData: TemplatePropertiesData) {
    lateinit var bigTitleBitmap: Bitmap
    lateinit var bigTitleShadowBitmap: Bitmap
    lateinit var smallTitleBitmap: Bitmap
    val bigTitleData = templatePropertiesData.bigTitleData
    val smallTitleData = templatePropertiesData.smallTitleData
    val lineData = templatePropertiesData.lineData
    val rectangleData = templatePropertiesData.rectangleData
    val bitmapData = templatePropertiesData.bitmapData
    val bitmapFromText = BitmapFromText()
    val bigTitlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    val smallTitlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    val lineShadowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    val rectPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        initTextBitmap()
        initPaints()
    }

    private fun initPaints() {
        // nothing to do for title paints
        if(lineData != null) {

            linePaint.color = lineData.lineColor
            linePaint.strokeWidth = lineData.lineWidth*width
            linePaint.style = Paint.Style.STROKE

            lineShadowPaint.color = Color.argb(50,0,0,0)
            lineShadowPaint.strokeWidth = lineData.lineWidth*width
            lineShadowPaint.style = Paint.Style.STROKE
        }

        if(rectangleData != null) {

        }

        if(bitmapData != null) {

        }
    }

    private fun initTextBitmap() {
        if(bigTitleData != null) {
            val w = (bigTitleData.right*width-bigTitleData.left*width)
            val h = (bigTitleData.bottom*height-bigTitleData.top*height)
            bigTitleBitmap = bitmapFromText.getBitmap(
                w.toInt(),h.toInt(),bigTitleData.text,true,
                bigTitleData.textColor,bigTitleData.backgroundColor, true
            )
            if(bigTitleData.shadow) {
                bigTitleShadowBitmap = bitmapFromText.getBitmap(
                    w.toInt(),h.toInt(),bigTitleData.text,true,
                    Color.argb(50,0,0,0),bigTitleData.backgroundColor,true
                )
            }
        }

        if(smallTitleData != null) {
            val w = (smallTitleData.right*width-smallTitleData.left*width)
            val h = (smallTitleData.bottom*height-smallTitleData.top*height)
            smallTitleBitmap = bitmapFromText.getBitmap(
                w.toInt(),h.toInt(),smallTitleData.text,true,
                smallTitleData.textColor,smallTitleData.backgroundColor, true
            )
        }
    }

    fun getBitmap(pTime: Long) : Bitmap {
        val bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        if(bigTitleData != null) { drawBigTitle(canvas, pTime) }

        if(smallTitleData != null) { drawSmallTitle(canvas,pTime) }

        if(lineData != null) { drawLine(canvas,pTime) }

        //if(rectangleData != null) { drawRectangle(canvas,pTime) }

        //if(bitmapData != null) { drawBitmap(canvas,pTime) }

        return bitmap
    }

    private fun drawBitmap(canvas: Canvas, pTime: Long) {
        TODO("Not yet implemented")
    }

    private fun drawRectangle(canvas: Canvas, pTime: Long) {
        TODO("Not yet implemented")
    }

    fun drawLine(canvas: Canvas,pTime: Long) {
        if(pTime < lineData!!.startTime) return

        val totalTime = lineData.endTime - lineData.startTime
        val ratio = min(1f, ((pTime - lineData.startTime).toFloat() / totalTime.toFloat()).pow(lineData.transitionPowerFactor))

        var startX: Float
        var startY: Float
        var endX: Float
        var endY: Float


        //---------------------------- transition part -------------------------------------------
        // TODO: 3/29/21 all transitions
        when(lineData.transitionType) {
            AppConstants.TransitionType.LEFT_TO_RIGHT -> {
                startX = (lineData.startX*width)
                startY = (lineData.startY*height)
                endX = ((lineData.endX*width - lineData.startX*width)*ratio) + startX
                endY = lineData.endY*height
            }
            AppConstants.TransitionType.RIGHT_TO_LEFT -> {
                startX = (lineData.endX*width)
                startY = (lineData.endY*height)
                endX = startX - ((lineData.endX*width - lineData.startX*width)*ratio)
                endY = lineData.startY*height
            }
            AppConstants.TransitionType.TOP_TO_BOTTOM -> {
                startX = (lineData.startX*width)
                startY = (lineData.startY*height)
                endX = (lineData.endX*width)
                endY = ((lineData.endY*height - lineData.startY*height)*ratio)+ startY
            }
            AppConstants.TransitionType.BOTTOM_TO_TOP -> {
                startX = lineData.endX*width
                startY = lineData.endY*height
                endX = startX
                endY = startY - ((lineData.endY*height-lineData.startY*height)*ratio)
            }
            AppConstants.TransitionType.SMALL_TO_LARGE -> {
                if(abs(lineData.startX - lineData.endX) < 0.000001) {
                    val centerY = (lineData.startY*height+lineData.endY*height)/2f
                    val len = (lineData.endY*height - lineData.startY*height)/2f
                    startX = lineData.startX*width
                    startY = centerY - len*ratio
                    endX = startX
                    endY = centerY + len*ratio
                }
                else {
                    val centerX = (lineData.startX*width+lineData.endX*width)/2f
                    val len = (lineData.endX*width-lineData.startX*width)/2f
                    startX = centerX - len*ratio
                    startY = lineData.startY*height
                    endX = centerX + len*ratio
                    endY = lineData.endY*height
                }
            }
            else -> {
                startX = lineData.startX
                startY = lineData.startY
                endX = lineData.endX
                endY = lineData.endY
            }
        }

        // rotation part -------------------------- rotation part ------------------------------
        var rotation = 0f
        rotation = when(lineData.rotationType) {

            AppConstants.RotationType.NO_ROTATION -> {
                0f
            }
            AppConstants.RotationType.CLOCKWISE -> {
                ratio*360f
            }
            AppConstants.RotationType.ANTI_CLOCKWISE -> {
                -ratio*360f
            }
            else -> {
                0f
            }
        }

        canvas.rotate(rotation,(startX+endX)/2f,(startY+endY)/2)
        if(lineData.shadow) {
            val shadowDiff = (linePaint.strokeWidth)/5f
            canvas.drawLine(startX+shadowDiff,startY+shadowDiff,endX+shadowDiff,endY+shadowDiff,lineShadowPaint)
        }
        canvas.drawLine(startX,startY,endX,endY,linePaint)
        canvas.rotate(-rotation,(startX+endX)/2f,(startY+endY)/2)
    }

    fun drawSmallTitle(canvas: Canvas, pTime: Long) {

        if(pTime < smallTitleData!!.startTime) return

        val totoalTime = smallTitleData.endTime - smallTitleData.startTime
        val ratio = min(1f, ((pTime - smallTitleData.startTime).toFloat() / totoalTime.toFloat()).pow(smallTitleData.transitionPowerFactor))

        var subRect: Rect
        var desRect: Rect

        // --------------------------- transition part -------------------------------
        when(smallTitleData.transitionType) {
            AppConstants.TransitionType.LEFT_TO_RIGHT -> {
                subRect = Rect(0, 0, (smallTitleBitmap.width * ratio).toInt(), smallTitleBitmap.height)
                desRect = Rect(
                    (smallTitleData.left * width).toInt(),
                    (smallTitleData.top * height).toInt(),
                    ((smallTitleBitmap.width * ratio) + smallTitleData.left * width).toInt(),
                    (smallTitleData.bottom * height).toInt())
            }
            AppConstants.TransitionType.RIGHT_TO_LEFT -> {
                subRect = Rect(0, 0, (smallTitleBitmap.width * ratio).toInt(), smallTitleBitmap.height)
                desRect = Rect(
                    ((smallTitleData.right *width) - smallTitleBitmap.width * ratio).toInt(),
                    (smallTitleData.top * height).toInt(),
                    (smallTitleData.right * width).toInt(),
                    (smallTitleData.bottom * height).toInt()
                )
            }
            AppConstants.TransitionType.TOP_TO_BOTTOM -> {
                subRect = Rect(0, 0, smallTitleBitmap.width, (smallTitleBitmap.height*ratio).toInt())
                desRect = Rect(
                    (smallTitleData.left * width).toInt(),
                    (smallTitleData.top * height).toInt(),
                    (smallTitleData.right * width).toInt(),
                    ((smallTitleBitmap.height * ratio) +(smallTitleData.top * height)).toInt()
                )
            }
            AppConstants.TransitionType.BOTTOM_TO_TOP -> {
                subRect = Rect(0, 0, smallTitleBitmap.width, (smallTitleBitmap.height*ratio).toInt())
                desRect = Rect(
                    (smallTitleData.left * width).toInt(),
                    ((smallTitleData.bottom * height) - (smallTitleBitmap.height * ratio)).toInt(),
                    (smallTitleData.right * width).toInt(),
                    (smallTitleData.bottom * height).toInt()
                )
            }
            AppConstants.TransitionType.SMALL_TO_LARGE -> {
                val desRectLen = (smallTitleData.right*width - smallTitleData.left*width)/2f
                val subRectLen = (smallTitleBitmap.width)/2f
                val desRectCenterX = (smallTitleData.left*width + smallTitleData.right*width)/2f
                val subRectCenterX = (smallTitleBitmap.width)/2f
                subRect = Rect(
                    (subRectCenterX - subRectLen*ratio).toInt(),0,
                    (subRectCenterX + subRectLen*ratio).toInt(),
                    smallTitleBitmap.height
                )
                desRect = Rect(
                    (desRectCenterX - desRectLen*ratio).toInt(),
                    (smallTitleData.top*height).toInt(),
                    (desRectCenterX + desRectLen*ratio).toInt(),
                    (smallTitleData.bottom*height).toInt()
                )
            }
            else -> {
                subRect = Rect(0,0,smallTitleBitmap.width,smallTitleBitmap.height)
                desRect = Rect(
                    (smallTitleData.left*width).toInt(),
                    (smallTitleData.top*height).toInt(),
                    (smallTitleData.right*width).toInt(),
                    (smallTitleData.bottom*height).toInt()
                )
            }
        }

        // rotation part -------------------------- rotation part ------------------------------
        var rotation = 0f
        when(smallTitleData.rotationType) {

            AppConstants.RotationType.CLOCKWISE -> {
                rotation = ratio*360f
            }
            AppConstants.RotationType.ANTI_CLOCKWISE -> {
                rotation = -ratio*360f
            }
            AppConstants.RotationType.NO_ROTATION -> {
                rotation = 0f
            }
            else -> {
                rotation = 0f
            }
        }

        canvas.rotate(rotation,(desRect.left+desRect.right)/2f,(desRect.top+desRect.bottom)/2f)
        canvas.drawBitmap(smallTitleBitmap, subRect, desRect, bigTitlePaint)
        canvas.rotate(-rotation,(desRect.left+desRect.right)/2f,(desRect.top+desRect.bottom)/2f)
    }

    fun drawBigTitle(canvas: Canvas, pTime: Long) {

        if(pTime < bigTitleData!!.startTime) return

        val totoalTime = bigTitleData.endTime - bigTitleData.startTime
        val ratio = min(1f, ((pTime - bigTitleData.startTime).toFloat() / totoalTime.toFloat()).pow(bigTitleData.transitionPowerFactor))

        var subRect: Rect
        var desRect: Rect

        // --------------------------- transition part -------------------------------

        when(bigTitleData.transitionType) {
            AppConstants.TransitionType.LEFT_TO_RIGHT -> {
                subRect = Rect(0, 0, (bigTitleBitmap.width * ratio).toInt(), bigTitleBitmap.height)
                desRect = Rect(
                    (bigTitleData.left * width).toInt(),
                    (bigTitleData.top * height).toInt(),
                    ((bigTitleBitmap.width * ratio) + bigTitleData.left * width).toInt(),
                    (bigTitleData.bottom * height).toInt())
            }
            AppConstants.TransitionType.RIGHT_TO_LEFT -> {
                subRect = Rect(0, 0, (bigTitleBitmap.width * ratio).toInt(), bigTitleBitmap.height)
                desRect = Rect(
                    ((bigTitleData.right *width) - bigTitleBitmap.width * ratio).toInt(),
                    (bigTitleData.top * height).toInt(),
                    (bigTitleData.right * width).toInt(),
                    (bigTitleData.bottom * height).toInt()
                )
            }
            AppConstants.TransitionType.TOP_TO_BOTTOM -> {
                subRect = Rect(0, 0, bigTitleBitmap.width, (bigTitleBitmap.height*ratio).toInt())
                desRect = Rect(
                    (bigTitleData.left * width).toInt(),
                    (bigTitleData.top * height).toInt(),
                    (bigTitleData.right * width).toInt(),
                    ((bigTitleBitmap.height * ratio) +(bigTitleData.top * height)).toInt()
                )
            }
            AppConstants.TransitionType.BOTTOM_TO_TOP -> {
                subRect = Rect(0, 0, bigTitleBitmap.width, (bigTitleBitmap.height*ratio).toInt())
                desRect = Rect(
                    (bigTitleData.left * width).toInt(),
                    ((bigTitleData.bottom * height) - (bigTitleBitmap.height * ratio)).toInt(),
                    (bigTitleData.right * width).toInt(),
                    (bigTitleData.bottom * height).toInt()
                )
            }
            AppConstants.TransitionType.SMALL_TO_LARGE -> {
                val desRectLen = (bigTitleData.right*width - bigTitleData.left*width)/2f
                val subRectLen = (bigTitleBitmap.width)/2f
                val desRectCenterX = (bigTitleData.left*width + bigTitleData.right*width)/2f
                val subRectCenterX = (bigTitleBitmap.width)/2f
                subRect = Rect(
                    (subRectCenterX - subRectLen*ratio).toInt(),0,
                    (subRectCenterX + subRectLen*ratio).toInt(),
                    bigTitleBitmap.height
                )
                desRect = Rect(
                    (desRectCenterX - desRectLen*ratio).toInt(),
                    (bigTitleData.top*height).toInt(),
                    (desRectCenterX + desRectLen*ratio).toInt(),
                    (bigTitleData.bottom*height).toInt()
                )
            }
            else -> {
                subRect = Rect(0,0,bigTitleBitmap.width,bigTitleBitmap.height)
                desRect = Rect(
                    (bigTitleData.left*width).toInt(),
                    (bigTitleData.top*height).toInt(),
                    (bigTitleData.right*width).toInt(),
                    (bigTitleData.bottom*height).toInt()
                )
            }
        }

        // rotation part -------------------------- rotation part ------------------------------
        var rotation = 0f
        rotation = when(bigTitleData.rotationType) {

            AppConstants.RotationType.CLOCKWISE -> {
                ratio*360f
            }
            AppConstants.RotationType.ANTI_CLOCKWISE -> {
                -ratio*360f
            }
            AppConstants.RotationType.NO_ROTATION -> {
                0f
            }
            else -> {
                0f
            }
        }

        canvas.rotate(rotation,(desRect.left+desRect.right)/2f,(desRect.top+desRect.bottom)/2f)
        if(bigTitleData.shadow) {
            val shadowDiff = 10f
            var shadowDesRect = Rect()
            shadowDesRect.left = desRect.left + shadowDiff.toInt()
            shadowDesRect.top = desRect.top + shadowDiff.toInt()
            shadowDesRect.right = desRect.right + shadowDiff.toInt()
            shadowDesRect.bottom = desRect.bottom + shadowDiff.toInt()
            canvas.drawBitmap(bigTitleShadowBitmap, subRect, shadowDesRect, bigTitlePaint)
        }
        canvas.drawBitmap(bigTitleBitmap, subRect, desRect, bigTitlePaint)
        canvas.rotate(-rotation,(desRect.left+desRect.right)/2f,(desRect.top+desRect.bottom)/2f)
    }
}