package com.example.animation_to_video.animation.drawelement

import android.content.Context
import android.graphics.*
import android.util.Log
import com.example.animation_to_video.R
import com.example.animation_to_video.animation.gl.LinePoint

/**
 * @author Md Jahirul Islam Hridoy
 * Created on 21,March,2022
 */
class DrawElementProcess(wid: Int, hei: Int, context: Context) {
    private val width = wid
    private val height = hei
    private val mContext = context
    var canvas = Canvas()
    private var p = 0f
    private var t = 0f

    private val tempBitmap: Bitmap

    val eleArrayList = ArrayList<LinePoint>()

    init {
        tempBitmap = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(mContext.resources, R.drawable.sound), width, height, false)

        val totalFram = 400
        val totTime = 4000000f
        for(i in 0..totalFram){
            val x = 16.0f * (i.toFloat() / totalFram.toFloat())
            val y = 4.5f
            val pTime = totTime * (i.toFloat() / totalFram.toFloat())
            eleArrayList.add(LinePoint(x, y, pTime))
        }
    }

    fun createBitmap(pTime: Long, totalTime: Long): Bitmap {
        p = pTime.toFloat()
        t = totalTime.toFloat()

        val RectPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        RectPaint.color = Color.BLACK

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        canvas = Canvas(bitmap)

        val lineWidth = (height/20).toFloat()

        Log.e(TAG, "createBitmap Default: width => $width height => $height presentTime -> $p tot => $t " )
        Log.e(TAG, "createBitmap 1: x1 => 0f y1 => 0f  x2 => ${(p / t) * width.toFloat()} y2 => $lineWidth " )
        Log.e(TAG, "createBitmap 2: x1 => 0f y1 => 0f  x2 => $lineWidth y2 => ${(p / t) * height.toFloat()} " )
        Log.e(TAG, "createBitmap 3: x1 => ${width.toFloat() - lineWidth} y1 => 0f  x2 => ${width.toFloat()} y2 => ${(p / t) * height.toFloat()} " )
        Log.e(TAG, "createBitmap 4: x1 => 0f y1 => ${height.toFloat() - lineWidth} x2 => ${(p / t) * width.toFloat()} y2 => ${height.toFloat()} " )

        mDrawRectangle(0f, 0f, (p / t) * width.toFloat(), lineWidth, getPaint(0))
        mDrawRectangle(0f, 0f, lineWidth, (p / t) * height.toFloat(), getPaint(0))
        mDrawRectangle(width.toFloat() - lineWidth, 0f, width.toFloat(), (p / t) * height.toFloat(), getPaint(0))
        mDrawRectangle(0f, height.toFloat() - lineWidth, (p / t) * width.toFloat(), height.toFloat(), getPaint(0))

        val circleX = (width/2).toFloat()
        val circleY = (height/2).toFloat()
        val circleRadius = (height/(2+1)).toFloat()

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true
        paint.strokeWidth = (p/t)*40f

        mDrawCircle(circleX, circleY, circleRadius, getPaint(1))

        mDrawLine()

        // draw vector
        val svg = mContext.resources.getDrawable(R.drawable.chill_time)

        svg.setBounds(0, 0, (p / t * width).toInt(), (p / t * height).toInt())
        svg.draw(canvas)
        //svg.alpha = (255f*(p/t)).toInt()


        // draw bitmap
        val srcRect = Rect(0, 0, (p / t * width).toInt(), height)
        canvas.drawBitmap(tempBitmap, srcRect, srcRect, RectPaint)


        // draw text as bitmap
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        textPaint.color = Color.MAGENTA
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 100f

        val text = "Hello There"
        val baseline = -textPaint.ascent() // ascent() is negative
        val textWidth = (textPaint.measureText(text) + 0.5f).toInt() // round
        val textHeight = (baseline + paint.descent() + 0.5f).toInt()
        val textBitmap = Bitmap.createBitmap(textWidth, textHeight, Bitmap.Config.ARGB_8888) // text as bitmap
        val textCanvas = Canvas(textBitmap)
        textCanvas.drawText(text, 0f,baseline,textPaint)

        //Log.d(TAG, "createBitmap: ${textBitmap.width} x ${textBitmap.height}")

        val textRect = Rect(0, 0, (p / t * width).toInt(), height)
        canvas.drawBitmap(textBitmap, textRect, textRect, RectPaint)
        //canvas.drawBitmap(textBitmap,0f,0f,RectPaint)

        return bitmap
    }

    private fun mDrawLine() {
        val elePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        elePaint.color = Color.BLUE
        elePaint.style = Paint.Style.STROKE
        elePaint.isAntiAlias = true
        elePaint.strokeWidth = 40f
        elePaint.strokeJoin = Paint.Join.ROUND

        val startX = eleArrayList[0].x * (width.toFloat() / 16.0f)
        val startY = eleArrayList[0].y * (height.toFloat() / 9.0f)
        var endX = -1.0f
        var endY = -1.0f
        for(i in 1 until eleArrayList.size){
            if(eleArrayList[i].pTime < p){
                endX = eleArrayList[i].x * (width.toFloat()/16.0f)
                endY = eleArrayList[i].y * (height.toFloat()/9.0f)
            }
            else break
        }

        if(endX > 0.0) canvas.drawLine(startX, startY, endX, endY, elePaint)
    }

    private fun getPaint(type: Int): Paint {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        when(type) {
            0 -> {
                paint.color = Color.RED
            }
            1 -> {
                paint.color = Color.BLACK
                paint.style = Paint.Style.STROKE
                paint.isAntiAlias = true
                paint.strokeWidth = (p / t) * 40f
            }
        }

        return paint
    }

    private fun mDrawRectangle(left: Float, top: Float, right: Float, bottom: Float, paint: Paint) {
        canvas.drawRect(left, top, right, bottom, paint)
    }

    private fun mDrawCircle(cx: Float, cy: Float, rad: Float, paint: Paint){
        canvas.drawCircle(cx, cy, rad, paint)
    }

    companion object {
        private const val TAG = "DrawElementProcess"
    }
}