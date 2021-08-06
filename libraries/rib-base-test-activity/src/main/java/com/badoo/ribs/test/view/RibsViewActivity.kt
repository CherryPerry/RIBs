package com.badoo.ribs.test.view

import android.os.Bundle
import android.widget.FrameLayout
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatActivity
import com.badoo.ribs.android.AndroidRibViewHost
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.core.view.ViewFactory

open class RibsViewActivity : AppCompatActivity() {

    lateinit var view: RibView
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME?.also { setTheme(it) }
        super.onCreate(savedInstanceState)
        val container = FrameLayout(this)
        setContentView(container)
        val rootViewHost = AndroidRibViewHost(container)
        view =
            requireNotNull(VIEW_FACTORY) { "Factory is not provided, do you use your JUnit Rule correctly?" }
                .invoke(ViewFactory.Context(rootViewHost, lifecycle))
    }

    companion object {
        @StyleRes
        var THEME: Int? = null
        var VIEW_FACTORY: ViewFactory<out RibView>? = null
    }

}
