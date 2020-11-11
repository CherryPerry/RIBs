package com.badoo.ribs.example.android

import android.app.Activity
import androidx.core.content.ContextCompat
import com.badoo.ribs.example.R

interface StatusBarController {
    fun applyStatusBarColor()
}

class StatusBarControllerImpl(
    val activity: Activity
) : StatusBarController {
    override fun applyStatusBarColor() {
        activity.window.statusBarColor = ContextCompat.getColor(activity, R.color.teel_200)
    }
}