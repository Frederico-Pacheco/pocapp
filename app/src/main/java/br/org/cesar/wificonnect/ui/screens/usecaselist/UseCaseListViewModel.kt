package br.org.cesar.wificonnect.ui.screens.usecaselist

import android.content.ComponentName
import android.content.ContentResolver
import androidx.lifecycle.ViewModel
import br.org.cesar.wificonnect.domain.usecase.accessibility.AccessibilityServiceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class UseCaseListViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(UseCaseListUiState())
    val uiState: StateFlow<UseCaseListUiState> = _uiState.asStateFlow()

    fun updateA11yState(
        contentResolver: ContentResolver,
    ) {
        val componentName = uiState.value.a11yServiceComponentName

        _uiState.update { currentState ->
            currentState.copy(
                isA11yEnabled = if (componentName != null) {
                    AccessibilityServiceUseCase.isServiceEnabled(contentResolver, componentName)
                } else {
                    null
                }
            )
        }
    }

    fun updateUiState(
        isA11yEnabled: Boolean? = null,
        a11yServiceComponentName: ComponentName? = null,
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                isA11yEnabled = isA11yEnabled ?: currentState.isA11yEnabled,
                a11yServiceComponentName = a11yServiceComponentName
                    ?: currentState.a11yServiceComponentName
            )
        }
    }
}