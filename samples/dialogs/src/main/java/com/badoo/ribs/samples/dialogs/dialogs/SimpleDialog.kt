package com.badoo.ribs.samples.dialogs.dialogs

import com.badoo.ribs.android.dialog.Dialog
import com.badoo.ribs.android.text.Text
import com.badoo.ribs.samples.dialogs.R

class SimpleDialog : Dialog<Dialog.Event>(
    {
        title = Text.Resource(R.string.simple_dialog_title)
        message = Text.Resource(R.string.dialog_text)
        buttons {
            positive(Text.Resource(R.string.dialog_positive_button), Event.Positive)
            negative(Text.Resource(R.string.dialog_negative_button), Event.Negative)
            neutral(Text.Resource(R.string.dialog_neutral_button), Event.Neutral)
        }
        cancellationPolicy = CancellationPolicy.Cancellable(
            event = Event.Cancelled,
            cancelOnTouchOutside = false
        )
    }
)
