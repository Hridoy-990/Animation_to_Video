package com.example.animation_to_video

/**
 * @author Md Jahirul Islam Hridoy
 * Created on 27,March,2022
 */
class AppConstants {
    companion object {
        const val SELECTED_TEMPLATE = "com.example.animation_to_video.SELECTED_TEMPLATE"
        const val FINAL_TEMPLATE_DATA = "com.example.animation_to_video.FINAL_TEMPLATE_DATA"
        const val HORIZONTAL = "horizontal"
        const val VERTICAL = "vertical"
        const val EXPAND = "expand"
        const val TEMPLATE = "template"
    }

    class BackgroundType {
        companion object {
            const val VIDEO = "video"
            const val IMAGE = "image"
            const val COLOR = "color"
        }
    }

    class TransitionType {
        companion object {
            const val LEFT_TO_RIGHT = "left-to-right"
            const val RIGHT_TO_LEFT = "right-to-left"
            const val TOP_TO_BOTTOM = "top-to-bottom"
            const val BOTTOM_TO_TOP = "bottom-to-top"
            const val FADE_OUT_TO_IN = "fade-out-to-in"
            const val FADE_IN_TO_OUT = "fade-in-to-out"
            const val SMALL_TO_LARGE = "small-to-large"
            const val HEIGHT_ZERO_TO_FULL = "height-zero-to-full"
            const val WIDTH_ZERO_TO_FULL = "width-zero-to-full"
            const val NO_TRANSITION = "no-transition"
        }
    }

    class RotationType {
        companion object {
            const val CLOCKWISE = "clockwise"
            const val ANTI_CLOCKWISE = "anti-clockwise"
            const val NO_ROTATION = "no-rotation"
        }
    }
}