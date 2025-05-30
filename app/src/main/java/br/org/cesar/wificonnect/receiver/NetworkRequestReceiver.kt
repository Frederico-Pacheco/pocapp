package br.org.cesar.wificonnect.receiver

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import br.org.cesar.wificonnect.R
import br.org.cesar.wificonnect.common.dispatcher.DispatcherProvider
import br.org.cesar.wificonnect.domain.usecase.network.NetworkRequestUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NetworkRequestReceiver : BroadcastReceiver() {
    private val receiverScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    @Inject
    lateinit var mDispatcherProvider: DispatcherProvider

    @Inject
    lateinit var mNetworkRequestUseCase: NetworkRequestUseCase

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action

        context?.let { ctx ->
            if (action == ctx.getString(R.string.action_request_wifi_connection)) {
                val ssid = intent.getStringExtra("WIFI_SSID")
                val psk = intent.getStringExtra("WIFI_PSK")

                performWifiRequest(context, ssid, psk)
            }
        }
    }

    private fun performWifiRequest(context: Context, ssid: String?, psk: String?) {
        val permissionStatus = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (permissionStatus == PackageManager.PERMISSION_GRANTED
            && !ssid.isNullOrEmpty() and !psk.isNullOrEmpty()
        ) {
            receiverScope.launch(mDispatcherProvider.io) {
                val requestDuration =
                    mNetworkRequestUseCase.measureNetworkRequest(ssid!!, psk!!, null)
            }
        }
    }
}