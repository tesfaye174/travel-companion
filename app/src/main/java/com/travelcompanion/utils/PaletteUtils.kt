package com.travelcompanion.utils

import android.content.Context
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.travelcompanion.R

object PaletteUtils {
    @ColorInt
    fun greenLight(context: Context) = ContextCompat.getColor(context, R.color.color_green_light)
    @ColorInt
    fun greenDark(context: Context) = ContextCompat.getColor(context, R.color.color_green_dark)
    @ColorInt
    fun black(context: Context) = ContextCompat.getColor(context, R.color.color_black)
    @ColorInt
    fun darkGray(context: Context) = ContextCompat.getColor(context, R.color.color_dark_gray)
    @ColorInt
    fun white(context: Context) = ContextCompat.getColor(context, R.color.color_white)
}
