package com.badoo.ribs.template.rib_with_view.foo_bar.mapper

import com.badoo.ribs.template.rib_with_view.foo_bar.FooBarView.Event
import com.badoo.ribs.template.rib_with_view.foo_bar.feature.FooBarFeature.Wish

internal object ViewEventToWish : (Event) -> Wish? {

    override fun invoke(event: Event): Wish? =
        TODO("Implement FooBarViewEventToWish mapping")
}
