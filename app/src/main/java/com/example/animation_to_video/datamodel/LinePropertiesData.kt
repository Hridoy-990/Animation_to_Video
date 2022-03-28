package com.example.animation_to_video.datamodel

import java.io.Serializable

/**
 * @author Md Jahirul Islam Hridoy
 * Created on 27,March,2022
 */
class LinePropertiesData (

    var lineColor: Int,
    var paintStyle: String,
    var lineWidth: Float, // width * linewidth will be actual width
    var startX: Float, // startx will startX*width
    var startY: Float, // startY*height
    var endX: Float,
    var endY: Float,
    var startTime: Long,
    var endTime: Long,
    var transitionType: String,
    var rotationType: String,
    var transitionPowerFactor: Float,
    var shadow: Boolean

) : Serializable {
}