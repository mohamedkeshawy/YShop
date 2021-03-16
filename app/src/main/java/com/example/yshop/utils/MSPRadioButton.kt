package com.example.yshop.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton

class MSPRadioButton(context: Context , attributeSet: AttributeSet) : AppCompatRadioButton( context , attributeSet) {

    init{
        applyFont()
    }

    private fun applyFont() {

        // This is used to get file from the assets folder and set it to the title textView
        val typeface: Typeface = Typeface.createFromAsset(context.assets, "Montserrat-Regular.ttf")
        setTypeface(typeface)
    }
}