package com.example.animation_to_video.animation.gl

/**
 * @author Md Jahirul Islam Hridoy
 * Created on 21,March,2022
 */
class BitmapTime {
    var deltaTimeSec: Float = 0f

        get() {
            if (lastUpdate == 0f)
                lastUpdate = System.currentTimeMillis().toFloat()
            return (System.currentTimeMillis().toFloat() / lastUpdate)/1000f
        }

    private var lastUpdate = 0f

    fun update() {
        lastUpdate = System.currentTimeMillis().toFloat()
    }
}