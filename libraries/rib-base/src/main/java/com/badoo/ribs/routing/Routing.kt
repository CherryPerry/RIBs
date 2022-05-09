package com.badoo.ribs.routing

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.io.Serializable


@Parcelize
data class Routing<C : Parcelable>(
    val configuration: C,
    val identifier: Identifier = Identifier(), // FIXME no default
    val meta: Meta = Meta.Empty
) : Parcelable {

    @Parcelize
    data class Identifier(
        val id: Serializable = 0 // FIXME no default // FIXME Parcelable
    ) : Parcelable


    interface Meta : Parcelable {

        /** Marks [Routing] as an overlay causing all other views in the same parent view to have importantForAccessibility=false. */
        val isOverlay: Boolean

        @Parcelize
        object Empty : Meta {
            @IgnoredOnParcel
            override val isOverlay: Boolean = false
        }

        @Parcelize
        data class Default(
            override val isOverlay: Boolean = false,
        ) : Meta

    }
}
