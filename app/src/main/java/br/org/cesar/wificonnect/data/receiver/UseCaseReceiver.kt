package br.org.cesar.wificonnect.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import br.org.cesar.wificonnect.R
import br.org.cesar.wificonnect.ui.MainActivity

class UseCaseReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action

        context?.let { ctx ->
            when (action) {
                ctx.getString(R.string.action_request_wifi_connection) -> {
                    performWifiRequest(context, intent)
                }

                ctx.getString(R.string.action_install_app) -> {
                    installPlayStoreApp(context, intent)
                }
            }
        }
    }

    private fun performWifiRequest(context: Context, intent: Intent) {
        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            action = "network.request"

            putExtra("WIFI_SSID", intent.getStringExtra("WIFI_SSID"))
            putExtra("WIFI_PSK", intent.getStringExtra("WIFI_PSK"))
        }

        context.startActivity(activityIntent)
    }

    private fun installPlayStoreApp(context: Context, intent: Intent) {
        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            action = "playstore.install"

            putExtra("APP_PKG", intent.getStringExtra("APP_PKG"))
            putExtra("APP_COMPANY", intent.getStringExtra("APP_COMPANY"))
        }

        context.startActivity(activityIntent)
    }
}