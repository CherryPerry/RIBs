package com.badoo.ribs.example.root.routing

import com.badoo.ribs.android.context.Scoped
import com.badoo.ribs.example.android.NavBarController
import com.badoo.ribs.example.android.NavBarControllerImpl
import com.badoo.ribs.example.auth.AuthDataSource
import com.badoo.ribs.example.logged_in_container.LoggedInContainer
import com.badoo.ribs.example.logged_in_container.LoggedInContainerBuilder
import com.badoo.ribs.example.logged_out_container.LoggedOutContainer
import com.badoo.ribs.example.logged_out_container.LoggedOutContainerBuilder
import com.badoo.ribs.example.root.Root

internal class RootChildBuilders(
    dependency: Root.Dependency,
    authDataSource: AuthDataSource
) {
    private val subtreeDeps = SubtreeDependency(
        dependency,
        authDataSource,
        dependency.statusBarController.map { NavBarControllerImpl(it) }
    )

    val loggedInContainerBuilder =
        LoggedInContainerBuilder(
            subtreeDeps
        )
    val loggedOutContainerBuilder =
        LoggedOutContainerBuilder(
            subtreeDeps
        )

    private class SubtreeDependency(
        dependency: Root.Dependency,
        override val authDataSource: AuthDataSource,
        override val navBarController: Scoped<NavBarController>
    ) : Root.Dependency by dependency,
        LoggedOutContainer.Dependency,
        LoggedInContainer.Dependency
}
