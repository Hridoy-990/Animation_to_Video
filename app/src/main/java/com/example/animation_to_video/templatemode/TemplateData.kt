package com.example.animation_to_video.templatemode

import android.graphics.Color
import com.example.animation_to_video.userinputtemplate.UserInputForTemplate

/**
 * @author Md Jahirul Islam Hridoy
 * Created on 23,March,2022
 */
class TemplateData {
    val data = ArrayList<UserInputForTemplate>()
    init {

/*        userInputForTemplate.selectedTemplate = 0
        userInputForTemplate.bigText = ""
        userInputForTemplate.bigTextColor = Color.TRANSPARENT
        userInputForTemplate.smallText = ""
        userInputForTemplate.smallTextColor = Color.TRANSPARENT
        userInputForTemplate.barColor = Color.TRANSPARENT
        userInputForTemplate.backgroundType = "color"
        userInputForTemplate.backgroundColor = Color.TRANSPARENT
        userInputForTemplate.backgroundImageUri = ""
        userInputForTemplate.backgroundVideoUri = ""
        userInputForTemplate.backgroundBrightness = 1.0f
        userInputForTemplate.isLogoShown =  false
        userInputForTemplate.logoUri = ""*/

        // template 0000000000000000000000000000000000000000000000000
        data.add(UserInputForTemplate(
            0,"Lii Lab", Color.WHITE,"abc",
            Color.TRANSPARENT, Color.WHITE,"video", Color.TRANSPARENT,
            "","",1.0f,false,"",
            720
        ))

        // template 1111111111111111111111111111111111111111111111111111111
        data.add(UserInputForTemplate(
            1,"Lii Lab", Color.WHITE,"Heart",
            Color.YELLOW, Color.RED,"video", Color.TRANSPARENT,
            "","",1.0f,false,"",
            720
        ))

        // tamplate 222222222222222222222222222222222222222222222222
        data.add(UserInputForTemplate(
            2,"Lii Lab", Color.RED,"Heart",
            Color.CYAN, Color.BLACK,"video", Color.TRANSPARENT,
            "","",1.0f,false,"",
            720
        ))

        // template 2+1 data
        data.add(
            UserInputForTemplate(
            2+1,"Lii Lab", Color.RED,"Heart",
            Color.CYAN, Color.WHITE,"video", Color.TRANSPARENT,
            "","",1.0f,false,"",
            720
        )
        )

        // template 44444444444444444444444444444444444444444444444444444444444444444444444
        data.add(UserInputForTemplate(
            4,"Lii Lab", Color.WHITE,"Heart",
            Color.RED, Color.YELLOW,"video", Color.TRANSPARENT,
            "","",1.0f,false,"",
            720
        ))

        // template 44444444444444444444444444444444444444444444444444444444444444444444444
        data.add(UserInputForTemplate(
            5,"Lii Lab", Color.WHITE,"Heart",
            Color.RED, Color.YELLOW,"video", Color.TRANSPARENT,
            "","",1.0f,false,"",
            720
        ))

    }
}