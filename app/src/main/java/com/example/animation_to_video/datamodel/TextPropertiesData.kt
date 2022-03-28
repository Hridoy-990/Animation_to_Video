package com.example.animation_to_video.datamodel

import java.io.Serializable

/**
 * @author Md Jahirul Islam Hridoy
 * Created on 27,March,2022
 */
class TextPropertiesData (

    var text: String,
    var font: String,
    var textColor: Int,
    var backgroundColor: Int,
    var textStyle: String,
    var left: Float,
    var top: Float,
    var right: Float,
    var bottom: Float,
    var startTime: Long,
    var endTime: Long,
    var transitionType: String,
    var rotationType: String,
    var transitionPowerFactor: Float,
    var shadow: Boolean,

    ) : Serializable {

}