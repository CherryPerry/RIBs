package com.badoo.ribs.store

import com.badoo.ribs.core.Rib
import kotlin.reflect.KClass

object Store {

    private val map: MutableMap<Rib.Identifier, MutableMap<KClass<*>, ValueHolder<*>>> = HashMap()

    inline fun <reified T : Any> get(
        owner: Rib.Identifier,
        noinline disposer: (T) -> Unit = {},
        noinline factory: () -> T
    ): T =
        get(owner, T::class, disposer, factory)

    fun <T : Any> get(owner: Rib.Identifier, clazz: KClass<*>, disposer: (T) -> Unit, factory: () -> T): T =
        map
            .getOrPut(owner) { HashMap() }
            .getOrPut(clazz) { ValueHolder(factory(), disposer) }
            .value as T


    fun removeAll(owner: Rib.Identifier) {
        map.remove(owner)?.values?.forEach { it.dispose() }
    }

    private class ValueHolder<T : Any>(
        val value: T,
        private val disposer: (T) -> Unit
    ) {
        fun dispose() {
            disposer(value)
        }
    }

}