package com.badoo.ribs.core.view

import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import com.badoo.ribs.core.Node
import com.badoo.ribs.core.modality.AncestryInfo
import com.badoo.ribs.core.plugin.Plugin

abstract class AndroidRibView : RibView {

    fun <T : View> findViewById(@IdRes id: Int): T =
        androidView.findViewById(id)

    protected open fun getParentViewForSubtree(subtreeOf: Node<*>): ViewGroup =
        androidView

    override fun attachChild(child: Node<*>, subtreeOf: Node<*>) {
        val target = getParentViewForSubtree(subtreeOf)
        child.onCreateView(this)
        child.view?.also { target.addView(it.androidView) }
        child.onAttachToView()
        child.plugins<AndroidViewLifecycleAware>().forEach {
            it.onAttachToView(target)
        }
        syncImportanceForAccessibilityWithOverlays(subtreeOf)
    }

    override fun detachChild(child: Node<*>, subtreeOf: Node<*>) {
        val target = getParentViewForSubtree(subtreeOf)
        child.view?.let { target.removeView(it.androidView) }
        child.onDetachFromView()
        child.plugins<AndroidViewLifecycleAware>().forEach {
            it.onDetachFromView(target)
        }
        syncImportanceForAccessibilityWithOverlays(subtreeOf)
    }

    private val Node<*>.isOverlay: Boolean
        get() = (ancestryInfo as? AncestryInfo.Child)?.creatorRouting?.meta?.isOverlay == true

    private fun syncImportanceForAccessibilityWithOverlays(parent: Node<*>) {
        val renderedOverlayNodes = parent.children.filter { it.isAttachedToView && it.isOverlay }
        parent.children.forEach { node ->
            if (node.isAttachedToView) {
                node.view?.androidView?.importantForAccessibility = when {
                    renderedOverlayNodes.isEmpty() -> View.IMPORTANT_FOR_ACCESSIBILITY_AUTO
                    node in renderedOverlayNodes -> View.IMPORTANT_FOR_ACCESSIBILITY_YES
                    else -> View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
                }
            }
        }
    }

    /**
     * Instances of this plugin will not be notified by Node, but by [AndroidRibView] directly,
     * as the existence of a parentViewGroup depends on this view type (think Compose).
     *
     * This also means that any child implementing this interface cannot be guaranteed to be notified
     * at all, only if attached to a parent that has [AndroidRibView].
     */
    interface AndroidViewLifecycleAware : Plugin {
        fun onAttachToView(parentViewGroup: ViewGroup) {}
        fun onDetachFromView(parentViewGroup: ViewGroup) {}
    }
}
