package com.badoo.ribs.android.requestcode

import com.badoo.ribs.android.requestcode.RequestCodeBasedEventStream.RequestCodeBasedEvent
import com.badoo.ribs.core.RequestCodeClient
import com.badoo.ribs.util.RIBs
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable

abstract class RequestCodeBasedEventStreamImpl<T : RequestCodeBasedEvent>(
    private val requestCodeRegistry: RequestCodeRegistry
) : RequestCodeBasedEventStream<T> {
    private val events = HashMap<Int, Relay<T>>()

    override fun events(client: RequestCodeClient): Observable<T> {
        val id = requestCodeRegistry.generateGroupId(client.id)
        ensureSubject(id)

        return events.getValue(id)
            .doOnDispose { cleanup(id) }
            .hide()
    }

    private fun ensureSubject(id: Int, onSubjectDidNotExist: (() -> Unit)? = null) {
        var subjectJustCreated = false

        if (!events.containsKey(id)) {
            events[id] = PublishRelay.create()
            subjectJustCreated = true
        }

        if (subjectJustCreated) {
            onSubjectDidNotExist?.invoke()
        }
    }

    private fun cleanup(id: Int) {
        events.remove(id)
    }

    protected fun publish(externalRequestCode: Int, event: T) {
        val id = requestCodeRegistry.resolveGroupId(externalRequestCode)
        val internalRequestCode = externalRequestCode.toInternalRequestCode()

        ensureSubject(id) {
            RIBs.errorHandler.handleNoRequestCodeListenersError(externalRequestCode, internalRequestCode, id, event)
        }

        events.getValue(id).accept(event)
    }

    protected fun Int.toInternalRequestCode() =
        requestCodeRegistry.resolveRequestCode(this)

    protected fun RequestCodeClient.forgeExternalRequestCode(internalRequestCode: Int) =
        requestCodeRegistry.generateRequestCode(this.id, internalRequestCode)
}
