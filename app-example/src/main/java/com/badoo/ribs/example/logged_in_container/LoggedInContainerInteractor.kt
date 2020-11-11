package com.badoo.ribs.example.logged_in_container

import androidx.lifecycle.Lifecycle
import com.badoo.mvicore.android.lifecycle.createDestroy
import com.badoo.ribs.android.context.Scoped
import com.badoo.ribs.android.subscribe
import com.badoo.ribs.clienthelper.interactor.Interactor
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.example.android.NavBarController
import com.badoo.ribs.example.auth.AuthDataSource
import com.badoo.ribs.example.logged_in_container.routing.LoggedInContainerRouter.Configuration
import com.badoo.ribs.routing.source.backstack.BackStackFeature

internal class LoggedInContainerInteractor(
    buildParams: BuildParams<*>,
    private val backStack: BackStackFeature<Configuration>,
    private val authDataSource: AuthDataSource,
    private val navBarController: Scoped<NavBarController>
) : Interactor<LoggedInContainer, Nothing>(
    buildParams = buildParams
) {

    override fun onCreate(nodeLifecycle: Lifecycle) {
        nodeLifecycle.createDestroy {
        }
        nodeLifecycle.subscribe(
            onStart = { navBarController.value.applyNavBarColor() }
        )
    }
}
