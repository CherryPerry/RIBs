package com.badoo.ribs.samples.gallery.rib.communication.picker

import com.badoo.ribs.rx2.clienthelper.connector.Connectable
import com.badoo.ribs.core.Rib
import com.badoo.ribs.samples.gallery.rib.communication.picker.CommunicationPicker.Input
import com.badoo.ribs.samples.gallery.rib.communication.picker.CommunicationPicker.Output
import com.bumble.appyx.utils.customisations.NodeCustomisation

interface CommunicationPicker : Rib, Connectable<Input, Output> {

    interface Dependency

    sealed class Input

    sealed class Output {
        object MenuExampleSelected : Output()
        object CoordinateMultipleSelected : Output()
    }

    class Customisation(
        val viewFactory: CommunicationPickerView.Factory = CommunicationPickerViewImpl.Factory()
    ) : NodeCustomisation
}
