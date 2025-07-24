package br.org.cesar.wificonnect.ui

import android.app.KeyguardManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import br.org.cesar.wificonnect.domain.usecase.dialer.DialerUseCase.Companion.EXTRA_CALL_NAME
import br.org.cesar.wificonnect.ui.screens.dialer.InCallScreenRoot
import br.org.cesar.wificonnect.ui.theme.DesignSystemTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class InCallUiActivity : ComponentActivity() {
    @Inject
    lateinit var mKeyguardManager: KeyguardManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTurnScreenOn(true)
        setShowWhenLocked(true)
        mKeyguardManager.requestDismissKeyguard(this, null)

        val callNumberFromIntent = intent.getStringExtra(EXTRA_CALL_NAME)
        setContent {
            DesignSystemTheme {
                InCallScreenRoot(
                    callNumber = callNumberFromIntent,
                    onFinishActivity = ::finish
                )
            }
        }
    }
}