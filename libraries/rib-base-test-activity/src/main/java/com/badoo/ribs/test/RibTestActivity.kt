package com.badoo.ribs.test

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.StyleRes
import com.badoo.ribs.android.RibActivity
import com.badoo.ribs.core.Rib

open class RibTestActivity : RibActivity() {

    lateinit var rib: Rib
        private set

    override lateinit var rootViewGroup: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME?.also { setTheme(it) }
        super.onCreate(savedInstanceState)
        rootViewGroup = FrameLayout(this)
        setContentView(rootViewGroup)
    }

    override fun createRib(savedInstanceState: Bundle?): Rib =
        requireNotNull(RIB_FACTORY) { "Factory is not provided, do you use your JUnit Rule correctly?" }
            .invoke(this, savedInstanceState)
            .also { rib = it }

    companion object {
        @StyleRes
        var THEME: Int? = null
        var RIB_FACTORY: ((RibTestActivity, Bundle?) -> Rib)? = null
    }
}
