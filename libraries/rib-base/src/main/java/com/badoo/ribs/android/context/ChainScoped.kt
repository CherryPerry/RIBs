package com.badoo.ribs.android.context

import androidx.lifecycle.Lifecycle
import com.badoo.ribs.android.subscribe

class ChainScoped<InitialValue, Value>(
    private val scoped: Scoped<InitialValue>,
    private val mapper: (InitialValue) -> Value,
    private val disposer: (Value) -> Unit = {}
) : Scoped<Value> {

    private var _value: Value? = null

    override val value: Value
        get() = _value ?: throw IllegalStateException("Lifecycle-bound value is already destroyed")

    override val optionalValue: Value?
        get() = _value

    init {
        lifecycle.subscribe(
            onCreate = { _value = mapper(scoped.value) },
            onDestroy = {
                _value?.also(disposer)
                _value = null
            }
        )
    }

    override fun getLifecycle(): Lifecycle =
        scoped.lifecycle

}