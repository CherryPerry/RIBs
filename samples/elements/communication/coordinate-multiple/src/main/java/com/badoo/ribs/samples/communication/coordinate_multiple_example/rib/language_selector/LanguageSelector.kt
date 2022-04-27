package com.badoo.ribs.samples.communication.coordinate_multiple_example.rib.language_selector

import com.badoo.ribs.clienthelper.connector.Connectable
import com.badoo.ribs.core.Rib
import com.badoo.ribs.samples.communication.coordinate_multiple_example.rib.language_selector.LanguageSelector.Output
import com.bumble.appyx.utils.customisations.NodeCustomisation

interface LanguageSelector : Rib, Connectable<Nothing, Output> {

    interface Dependency

    sealed class Output {
        data class LanguageSelected(val selectedIndex: Int) : Output()
    }

    class Customisation(
        val viewFactory: LanguageSelectorView.Factory = LanguageSelectorViewImpl.Factory()
    ) : NodeCustomisation
}
