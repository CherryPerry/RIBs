package com.badoo.ribs.test.view

import androidx.annotation.StyleRes
import androidx.test.rule.ActivityTestRule
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.core.view.ViewFactory

open class RibsViewRule<View : RibView>(
    private val launchActivity: Boolean = true,
    @StyleRes private val theme: Int? = null,
    private var viewFactory: ViewFactory<View>,
) : ActivityTestRule<RibsViewActivity>(RibsViewActivity::class.java, true, launchActivity) {

    @Suppress("UNCHECKED_CAST")
    val view: View
        get() = activity.view as View

    override fun beforeActivityLaunched() {
        super.beforeActivityLaunched()
        setup()
    }

    override fun afterActivityFinished() {
        super.afterActivityFinished()
        reset()
    }

    private fun setup() {
        RibsViewActivity.THEME = theme
        RibsViewActivity.VIEW_FACTORY = viewFactory
    }

    private fun reset() {
        RibsViewActivity.THEME = null
        RibsViewActivity.VIEW_FACTORY = null
    }

    fun start() {
        require(!launchActivity) {
            "Activity will be launched automatically, launchActivity parameter was passed into constructor"
        }
        launchActivity(null)
    }

}
