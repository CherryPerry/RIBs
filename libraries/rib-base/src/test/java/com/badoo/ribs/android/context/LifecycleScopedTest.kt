package com.badoo.ribs.android.context

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import org.junit.Assert.*
import org.junit.Test

class LifecycleScopedTest {

    private val value = LifecycleScoped<Any>()
    private val lifecycle = createLifecycle()

    @Test
    fun `Returns null in optionalValue after creation`() {
        assertNull(value.optionalValue)
    }

    @Test
    fun `Throws exception in value after creation`() {
        try {
            value.value
            fail()
        } catch (exception: IllegalStateException) {
            assertEquals(LifecycleScoped.ERROR_NOT_ATTACHED, exception.message)
        }
    }

    @Test
    fun `Returns null in optional value before lifecycle event`() {
        val obj = Any()
        value.attach(lifecycle) { obj }
        assertNull(value.optionalValue)
    }

    @Test
    fun `Returns value when ON_CREATE after attach`() {
        val obj = Any()
        value.attach(lifecycle) { obj }
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        assertEquals(obj, value.value)
    }

    @Test
    fun `Returns null and disposes value after ON_DESTROY`() {
        val obj = Any()
        var disposed = false
        value.attach(lifecycle, disposer = { disposed = true }) { obj }
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        assertNull(value.optionalValue)
        assertTrue(disposed)
    }

    @Test
    fun `Returns value when ON_CREATE before attach`() {
        val obj = Any()
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        value.attach(lifecycle) { obj }
        assertEquals(obj, value.value)
    }

    @Test
    fun `Returns null and disposes value when new lifecycle attached`() {
        val obj = Any()
        var disposed = false
        val disposer: (Any) -> Unit = { disposed = true }
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        value.attach(lifecycle, disposer) { obj }
        val newLifecycle = createLifecycle()
        value.attach(newLifecycle, disposer) { Any() }
        assertNull(value.optionalValue)
        assertTrue(disposed)
    }

    @Test
    fun `Returns new value when new created lifecycle attached`() {
        val obj = Any()
        val obj2 = Any()
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        value.attach(lifecycle) { obj }
        val newLifecycle = createLifecycle()
        newLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        value.attach(newLifecycle) { obj2 }
        assertEquals(obj2, value.value)
    }

    private fun createLifecycle(): LifecycleRegistry =
        LifecycleHelper().registry

    private class LifecycleHelper : LifecycleOwner {
        val registry = LifecycleRegistry(this)
        override fun getLifecycle(): Lifecycle = registry
    }

}