package br.org.cesar.wificonnect.ui

import android.Manifest
import android.app.KeyguardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.telecom.TelecomManager
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DialerActivity : ComponentActivity() {
    @Inject
    lateinit var mKeyguardManager: KeyguardManager

    @Inject
    lateinit var mTelecomManager: TelecomManager

    @RequiresPermission(Manifest.permission.CALL_PHONE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleDialIntent(intent)

        finish()
    }

    @RequiresPermission(Manifest.permission.CALL_PHONE)
    private fun handleDialIntent(intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_DIAL -> launchDialIntent(intent.data)
        }
    }

    private fun launchDialIntent(numberUri: Uri?) {
        numberUri?.let { uri ->
            val callIntent = Intent(Intent.ACTION_CALL).apply {
                data = uri
            }

            if (hasCallPhonePermission(Manifest.permission.CALL_PHONE)) {
                startActivity(callIntent)
            }
        }
    }

    private fun hasCallPhonePermission(permission: String): Boolean {
        val hasPermission = ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), 1)
        }

        return hasPermission
    }
}