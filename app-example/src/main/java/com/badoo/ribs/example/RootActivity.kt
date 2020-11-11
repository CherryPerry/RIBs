package com.badoo.ribs.example

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.ViewGroup
import com.badoo.ribs.android.RibActivity
import com.badoo.ribs.android.context.Scoped
import com.badoo.ribs.annotation.ExperimentalApi
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.modality.BuildContext.Companion.root
import com.badoo.ribs.example.android.StatusBarController
import com.badoo.ribs.example.android.StatusBarControllerImpl
import com.badoo.ribs.example.auth.AuthStateStorage
import com.badoo.ribs.example.auth.PreferencesAuthStateStorage
import com.badoo.ribs.example.network.ApiFactory
import com.badoo.ribs.example.network.UnsplashApi
import com.badoo.ribs.example.root.Root
import com.badoo.ribs.example.root.RootBuilder
import com.badoo.ribs.portal.Portal
import com.badoo.ribs.portal.PortalBuilder
import com.badoo.ribs.routing.resolution.ChildResolution.Companion.child
import com.badoo.ribs.routing.resolution.Resolution

@ExperimentalApi
class RootActivity : RibActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.example_activity_root)
        getActivityScoped<StatusBarController>().attach(lifecycle) { StatusBarControllerImpl(this) }
        super.onCreate(savedInstanceState)
    }

    override val rootViewGroup: ViewGroup
        get() = findViewById(R.id.root)

    override fun createRib(savedInstanceState: Bundle?): Rib {
        val statusBar = getActivityScoped<StatusBarController>()
        val storage = PreferencesAuthStateStorage(PreferenceManager.getDefaultSharedPreferences(this@RootActivity))
        return PortalBuilder(
            object : Portal.Dependency {
                override fun defaultResolution(): (Portal.OtherSide) -> Resolution =
                    { portal ->
                        child { buildContext ->
                            RootBuilder(
                                object : Root.Dependency {
                                    override val api: UnsplashApi =
                                        ApiFactory.api(BuildConfig.DEBUG, BuildConfig.ACCESS_KEY)
                                    override val authStateStorage: AuthStateStorage =
                                        storage
                                    override val statusBarController: Scoped<StatusBarController> =
                                        statusBar
                                }
                            ).build(buildContext)
                        }
                    }
            }
        ).build(root(savedInstanceState))
    }

}
