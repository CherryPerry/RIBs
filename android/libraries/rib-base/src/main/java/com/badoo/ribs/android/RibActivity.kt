package com.badoo.ribs.android

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import com.badoo.ribs.core.Node
import com.badoo.ribs.android.requestcode.RequestCodeRegistry

abstract class RibActivity : AppCompatActivity(), IntentCreator {

    private lateinit var requestCodeRegistry: RequestCodeRegistry

    protected val activityStarter: ActivityStarterImpl by lazy {
        ActivityStarterImpl(
            activity = this,
            intentCreator = this,
            requestCodeRegistry = requestCodeRegistry
        )
    }

    protected  val permissionRequester: PermissionRequesterImpl by lazy {
        PermissionRequesterImpl(
            activity = this,
            requestCodeRegistry = requestCodeRegistry
        )
    }

    private lateinit var rootNode: Node<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestCodeRegistry = RequestCodeRegistry(savedInstanceState)

        rootNode = createRib().apply {
                onAttach(savedInstanceState)
                attachToView(rootViewGroup)
        }
    }

    abstract val rootViewGroup: ViewGroup

    abstract fun createRib(): Node<*>

    override fun onStart() {
        super.onStart()
        rootNode.onStart()
    }

    override fun onStop() {
        super.onStop()
        rootNode.onStop()
    }

    override fun onPause() {
        super.onPause()
        rootNode.onPause()
    }

    override fun onResume() {
        super.onResume()
        rootNode.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        rootNode.onSaveInstanceState(outState)
        requestCodeRegistry.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        rootNode.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        rootNode.onDetach()
        rootNode.detachFromView(findViewById(android.R.id.content))
    }

    override fun onBackPressed() {
        if (!rootNode.handleBackPress()) {
            super.onBackPressed()
        }
    }

    override fun create(cls: Class<*>?): Intent =
        cls?.let { Intent(this, it) } ?: Intent()


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        activityStarter.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) =
        permissionRequester.onRequestPermissionsResult(requestCode, permissions, grantResults)

}
