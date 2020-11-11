package com.badoo.ribs.example.root

import com.badoo.ribs.android.context.Scoped
import com.badoo.ribs.clienthelper.connector.Connectable
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.customisation.RibCustomisation
import com.badoo.ribs.example.android.StatusBarController
import com.badoo.ribs.example.auth.AuthStateStorage
import com.badoo.ribs.example.network.UnsplashApi
import com.badoo.ribs.example.root.Root.Input
import com.badoo.ribs.example.root.Root.Output
import com.badoo.ribs.example.root.routing.RootRouter
import com.badoo.ribs.routing.transition.handler.TransitionHandler

interface Root : Rib, Connectable<Input, Output> {

    interface Dependency {
        val api: UnsplashApi
        val authStateStorage: AuthStateStorage
        val statusBarController: Scoped<StatusBarController>
    }

    sealed class Input

    sealed class Output

    class Customisation(
        val transitionHandler: TransitionHandler<RootRouter.Configuration>? = null
    ) : RibCustomisation
}
