package com.example.animation_to_video.userinputtemplate

import java.io.Serializable

/**
 * @author Md Jahirul Islam Hridoy
 * Created on 23,March,2022
 */
class UserInputForTemplate(
    var selectedTemplate: Int,
    var bigText: String,
    var bigTextColor: Int,
    var smallText: String,
    var smallTextColor: Int,
    var barColor: Int,
    var backgroundType: String,
    var backgroundColor: Int,
    var backgroundImageUri: String,
    var backgroundVideoUri: String,
    var backgroundBrightness: Float,
    var isLogoShown: Boolean,
    var logoUri: String,
    var videoResolution: Int
) : Serializable {
/*    var selectedTemplate: Int = 0
    var bigText: String = ""
    var bigTextColor = Color.TRANSPARENT
    var smallText: String = ""
    var smallTextColor = Color.TRANSPARENT
    var barColor = Color.TRANSPARENT
    var backgroundType: String = "color"
    var backgroundColor = Color.TRANSPARENT
    var backgroundImageUri: String = ""
    var backgroundVideoUri: String = ""
    var backgroundBrightness: Float = 1.0f
    var isLogoShown: Boolean = false
    var logoUri: String = ""
    var videoResolution = "720"*/
}