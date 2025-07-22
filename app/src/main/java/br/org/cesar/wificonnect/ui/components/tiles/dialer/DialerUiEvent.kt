package br.org.cesar.wificonnect.ui.components.tiles.dialer

sealed interface DialerUiEvent {
    data object RegisterDialerChangedReceiver : DialerUiEvent

    data object UnregisterDialerChangedReceiver : DialerUiEvent

    data object CheckDefaultDialerStatus : DialerUiEvent

    data object StartCall : DialerUiEvent

    data object EndCall : DialerUiEvent

    data class UpdateCallDirection(val direction: Int) : DialerUiEvent

    data class UpdateSpeakerState(val isSpeakerOn: Boolean) : DialerUiEvent
}