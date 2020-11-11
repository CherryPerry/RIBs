package com.badoo.ribs.example.android

import androidx.core.content.ContextCompat
import com.badoo.ribs.example.R

interface NavBarController {
    fun applyNavBarColor()
}

class NavBarControllerImpl(
    private val statusBarController: StatusBarController
) : NavBarController {
    override fun applyNavBarColor() {
        val activity = (statusBarController as StatusBarControllerImpl).activity
        activity.window.navigationBarColor = ContextCompat.getColor(activity, R.color.teel_200)
    }
}