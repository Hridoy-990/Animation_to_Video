package com.example.animation_to_video.datamodel

import java.io.Serializable

/**
 * @author Md Jahirul Islam Hridoy
 * Created on 27,March,2022
 */
class TemplateDataSingle (
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
) : Serializable{}