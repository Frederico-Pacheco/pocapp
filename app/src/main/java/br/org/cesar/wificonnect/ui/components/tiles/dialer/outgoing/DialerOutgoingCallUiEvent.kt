package br.org.cesar.wificonnect.ui.components.tiles.dialer.outgoing

sealed interface DialerOutgoingCallUiEvent {
    data object UpdateDialIntent : DialerOutgoingCallUiEvent
}