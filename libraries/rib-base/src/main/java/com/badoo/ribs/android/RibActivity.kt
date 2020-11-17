package com.badoo.ribs.android

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.badoo.ribs.android.activitystarter.ActivityBoundary
import com.badoo.ribs.android.activitystarter.ActivityStarter
import com.badoo.ribs.android.context.LifecycleScoped
import com.badoo.ribs.android.dialog.Dialog
import com.badoo.ribs.android.dialog.DialogLauncher
import com.badoo.ribs.android.dialog.toAlertDialog
import com.badoo.ribs.android.permissionrequester.PermissionRequestBoundary
import com.badoo.ribs.android.permissionrequester.PermissionRequester
import com.badoo.ribs.android.requestcode.RequestCodeRegistry
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.view.RibView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import kotlin.collections.HashMap
import kotlin.reflect.KClass

/**
 * Helper class for root [Rib] integration.
 *
 * Also offers base functionality to satisfy dependencies of Android-related functionality
 * down the tree:
 * - [DialogLauncher]
 * - [ActivityStarter]
 *
 * Feel free to not extend this and use your own integration point - in this case,
 * don't forget to take a look here what methods needs to be forwarded to the root Node.
 */
abstract class RibActivity : AppCompatActivity(), DialogLauncher {

    private val dialogs: WeakHashMap<Dialog<*>, AlertDialog> =
        WeakHashMap()

    private lateinit var requestCodeRegistry: RequestCodeRegistry

    private val activityBoundary: ActivityBoundary by lazy {
        ActivityBoundary(
            activity = this,
            requestCodeRegistry = requestCodeRegistry
        )
    }

    val activityStarter: ActivityStarter
        get() = activityBoundary

    private val permissionRequestBoundary: PermissionRequestBoundary by lazy {
        PermissionRequestBoundary(
            activity = this,
            requestCodeRegistry = requestCodeRegistry
        )
    }

    val permissionRequester: PermissionRequester
        get() = permissionRequestBoundary

    protected open lateinit var root: Rib
    protected open lateinit var rootViewHost: RibView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestCodeRegistry = RequestCodeRegistry(savedInstanceState)

        val rootHolder by viewModels<RootHolderImpl>()
        root = rootHolder.root ?: createRib(savedInstanceState).also {
            rootHolder.root = it
            it.node.onCreate()
        }
        rootViewHost = AndroidRibViewHost(rootViewGroup)
        rootViewHost.attachChild(root.node)

        if (intent?.action == Intent.ACTION_VIEW) {
            handleDeepLink(intent)
        }
    }

    private val disposables = CompositeDisposable()

    fun handleDeepLink(intent: Intent) {
        workflowFactory.invoke(intent)?.let {
            disposables.add(it.subscribe())
        }
    }

    open val workflowFactory: (Intent) -> Observable<*>? = {
        null
    }

    abstract val rootViewGroup: ViewGroup

    abstract fun createRib(savedInstanceState: Bundle?): Rib

    override fun onStart() {
        super.onStart()
        root.node.onStart()
    }

    override fun onStop() {
        super.onStop()
        root.node.onStop()
    }

    override fun onPause() {
        super.onPause()
        root.node.onPause()
    }

    override fun onResume() {
        super.onResume()
        root.node.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        root.node.onSaveInstanceState(outState)
        requestCodeRegistry.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        root.node.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        dialogs.values.forEach { it.dismiss() }
        rootViewHost.detachChild(root.node)
    }

    override fun onBackPressed() {
        if (!root.node.handleBackPress()) {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        activityBoundary.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) =
        permissionRequestBoundary.onRequestPermissionsResult(requestCode, permissions, grantResults)

    override fun show(dialog: Dialog<*>, onClose: () -> Unit) {
        dialogs[dialog] = dialog.toAlertDialog(this, onClose).also {
            it.show()
        }
    }

    override fun hide(dialog: Dialog<*>) {
        dialogs[dialog]?.dismiss()
    }

    @Suppress("UNCHECKED_CAST")
    protected inline fun <reified Value> getActivityScoped(): LifecycleScoped<Value> {
        val rootHolder by viewModels<RootHolderImpl>()
        var value = rootHolder.scoped[Value::class] as LifecycleScoped<Value>?
        if (value == null) {
            value = LifecycleScoped()
            rootHolder.scoped[Value::class] = value
        }
        return value
    }

    class RootHolderImpl : ViewModel() {
        val scoped = HashMap<KClass<*>, LifecycleScoped<*>>()
        var root: Rib? = null

        override fun onCleared() {
            super.onCleared()
            // onCleared is invoked before Activity onDestroy
            Handler(Looper.getMainLooper()).post {
                root?.node?.onDestroy()
                root = null
            }
        }
    }
}
