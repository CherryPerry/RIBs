package com.badoo.ribs.android.context

import androidx.lifecycle.LifecycleOwner

interface Scoped<Value> : LifecycleOwner {
    val value: Value
    val optionalValue: Value?

    fun <MapResult> map(disposer: (MapResult) -> Unit = {}, mapper: (Value) -> MapResult): Scoped<MapResult> =
        ChainScoped(this, mapper, disposer)

}