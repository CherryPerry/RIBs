package com.badoo.ribs.android.context

import androidx.lifecycle.*
import java.lang.ref.WeakReference

class LifecycleScoped<Value> : Scoped<Value> {
    private val lifecycleOwner = LifecycleRegistry(this)
    private var isAttachedAtLeastOnce = false
    private var parentLifecycle: WeakReference<Lifecycle>? = null
    private var lifecycleObserver: LifecycleObserver? = null
    private var _value: Value? = null

    override val value: Value
        get() = _value
            ?: throw IllegalStateException(if (isAttachedAtLeastOnce) ERROR_DESTROYED else ERROR_NOT_ATTACHED)

    override val optionalValue: Value?
        get() = _value

    override fun getLifecycle(): Lifecycle =
        lifecycleOwner

    fun attach(lifecycle: Lifecycle, disposer: (Value) -> Unit = {}, factory: () -> Value) {
        isAttachedAtLeastOnce = true
        // Remove observation from previous lifecycle
        parentLifecycle?.get()?.apply { removeObserver(lifecycleObserver!!) }
        // Dispose current value
        disposeValue(disposer)
        // Dispose all chained values
        if (lifecycleOwner.currentState == Lifecycle.State.CREATED) {
            lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        }
        // Observe new lifecycle
        lifecycleObserver = object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                _value = factory()
                lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
            }

            override fun onDestroy(owner: LifecycleOwner) {
                lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                disposeValue(disposer)
            }
        }.also(lifecycle::addObserver)
        parentLifecycle = WeakReference(lifecycle)
    }

    private fun disposeValue(disposer: (Value) -> Unit) {
        _value?.also(disposer)
        _value = null
    }

    internal companion object {
        internal const val ERROR_DESTROYED = "Lifecycle-bound value is already destroyed"
        internal const val ERROR_NOT_ATTACHED = "Call attach() first"
    }

}