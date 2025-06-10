package br.org.cesar.wificonnect.domain.usecase.network.wifirequest

import android.Manifest
import android.net.ConnectivityManager
import android.net.MacAddress
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.net.NetworkRequest
import android.net.NetworkSpecifier
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.util.Log
import androidx.annotation.RequiresPermission
import br.org.cesar.wificonnect.domain.usecase.UseCaseListener
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class NetworkRequestUseCase @Inject constructor(
    private val mWifiManager: WifiManager,
    private val mConnectivityManager: ConnectivityManager,
    private val mNetworkScanner: NetworkScanner,
) {

    private var mNetworkCallback: NetworkCallback? = null

    fun isWifiEnabled(): Boolean {
        return mWifiManager.isWifiEnabled
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun measureNetworkRequest(
        ssid: String?,
        psk: String?,
        listener: UseCaseListener? = null
    ): Long? {
        listener?.onUseCaseStarted()

        if (ssid.isNullOrEmpty() or psk.isNullOrEmpty()) {
            notifyError("Missing SSID or PSK", listener)
            return null
        }

        Log.v(TAG, "Scan and find the network: $ssid")
        listener?.onUseCaseMsgReceived("Initiating scan for network: $ssid")

        val testNetwork: ScanResult? = mNetworkScanner.startScanAndFindAMatchingNetwork(ssid!!)
        if (testNetwork == null) {
            notifyError(
                "Unable to initiate scan or find matching network in scan results.",
                listener
            )
            return null
        }

        mNetworkCallback = NetworkCallback(CALLBACK_TIMEOUT_MS)
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(TRANSPORT_WIFI)
            .setNetworkSpecifier(createNetworkSpecifier(testNetwork, psk!!))
            .removeCapability(NET_CAPABILITY_INTERNET)
            .build()

        listener?.onUseCaseMsgReceived("Initiating network request.")
        val startTime = System.currentTimeMillis()
        mConnectivityManager.requestNetwork(
            networkRequest, mNetworkCallback!!,
            NETWORK_REQUEST_TIMEOUT_MS
        )

        listener?.onUseCaseMsgReceived("Waiting for network connection. Please click the network in the dialog that pops up for approving the request.")

        val (isAvailable, _) = mNetworkCallback!!.waitForAvailable()
        if (!isAvailable) {
            notifyError("Failed to get network available callback", listener)
            return null
        }

        val endTime = System.currentTimeMillis()
        listener?.onUseCaseMsgReceived("Connected to network")
        listener?.onUseCaseSuccess()

        return endTime - startTime
    }

    fun unregisterNetworkCallback(listener: UseCaseListener? = null) {
        mNetworkCallback?.let {
            mConnectivityManager.unregisterNetworkCallback(it)
            listener?.onUseCaseMsgReceived("Network callback disconnected")
        }
    }

    private fun createNetworkSpecifier(scanResult: ScanResult, psk: String): NetworkSpecifier {
        val configBuilder = WifiNetworkSpecifier.Builder()

        configBuilder.setSsid(scanResult.getSsid());
        configBuilder.setBssid(MacAddress.fromString(scanResult.BSSID));
        if (psk.isNotEmpty()) {
            if (scanResult.capabilities.contains("PSK")) {
                configBuilder.setWpa2Passphrase(psk);
            } else if (scanResult.capabilities.contains("SAE")) {
                configBuilder.setWpa3Passphrase(psk);
            }
        }

        return configBuilder.build();
    }

    private fun notifyError(msg: String, listener: UseCaseListener?) {
        Log.e(TAG, msg)
        listener?.onUseCaseMsgReceived(msg)
        listener?.onUseCaseFailed(msg)
    }

    companion object {
        private val TAG = NetworkRequestUseCase::class.simpleName
        private const val NETWORK_REQUEST_TIMEOUT_MS: Int = 30000
        private const val CALLBACK_TIMEOUT_MS: Long = 40000
    }
}