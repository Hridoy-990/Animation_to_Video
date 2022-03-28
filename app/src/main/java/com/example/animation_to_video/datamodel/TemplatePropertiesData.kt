package com.example.animation_to_video.datamodel

import java.io.Serializable

/**
 * @author Md Jahirul Islam Hridoy
 * Created on 27,March,2022
 */
class TemplatePropertiesData (

    var bigTitleData: TextPropertiesData?,
    var smallTitleData: TextPropertiesData?,
    var lineData: LinePropertiesData?,
    var rectangleData: RectanglePropertiesData?,
    var bitmapData: BitmapPropertiesData?,
    var backgroundType: String,
    var backgroundColor: Int,
    var backgroundImageUri: String,
    var backgroundVideoUri: String,
    var backgroundBrightness: Float,
    var videoResolution: Int,
    var templateNumber: Int,
    var templatePreviewImageId: Int,
    var templatePreviewVideoUri: String

) : Serializable {

}