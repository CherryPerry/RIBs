package com.badoo.ribs.core.helper

import android.view.ViewGroup
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.Node
import com.badoo.ribs.core.view.RibView
import com.nhaarman.mockitokotlin2.mock

class TestView : RibView {

    override val androidView: ViewGroup = mock()

    override fun getParentViewForChild(child: Node<*>) =
        androidView
}
