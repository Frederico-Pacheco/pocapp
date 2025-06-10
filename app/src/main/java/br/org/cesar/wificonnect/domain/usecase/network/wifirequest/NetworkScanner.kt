package br.org.cesar.wificonnect.domain.usecase.network.wifirequest

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresPermission
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@ViewModelScoped
class NetworkScanner @Inject constructor(
    @ApplicationContext private val mContext: Context,
    private val mWifiManager: WifiManager,
) {

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    @Throws(SecurityException::class)
    fun startScanAndFindAMatchingNetwork(ssid: String): ScanResult? {
        // Start scan and wait for new results.
        if (!startScanAndWaitForResults()) {
            Log.e(TAG, "Failed to initiate a new scan. Using cached results from device")
        }

        // Filter results to find an open network.
        val scanResults: List<ScanResult> = mWifiManager.scanResults
        for (scanResult in scanResults) {
            if (TextUtils.equals(ssid, scanResult.getSsid())
                && !TextUtils.isEmpty(scanResult.BSSID)
            ) {
                Log.v(TAG, "Found network $scanResult")
                return scanResult
            }
        }


        Log.e(TAG, "No matching network found in scan results")
        return null
    }

    private fun startScanAndWaitForResults(): Boolean {
        val intentFilter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        val countDownLatch = CountDownLatch(1)

        // Scan Results available broadcast receiver.
        val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Log.v(TAG, "BroadcastReceiver: Broadcast onReceive $intent")
                if (intent.action != WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) return
                if (!intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)) return
                Log.v(TAG, "BroadcastReceiver: Scan results received")
                countDownLatch.countDown()
            }
        }

        // Register the receiver for scan results broadcast.
        mContext.registerReceiver(broadcastReceiver, intentFilter)

        // Start scan.
        Log.v(TAG, "Starting scan")
        if (!mWifiManager.startScan()) {
            Log.e(TAG, "Failed to start scan")

            // Unregister the receiver for scan results broadcast.
            mContext.unregisterReceiver(broadcastReceiver)
            return false
        }

        // Wait for scan results.
        Log.v(TAG, "Wait for scan results")
        if (!countDownLatch.await(SCAN_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
            Log.e(TAG, "No new scan results available")

            // Unregister the receiver for scan results broadcast.
            mContext.unregisterReceiver(broadcastReceiver)
            return false
        }

        // Unregister the receiver for scan results broadcast.
        mContext.unregisterReceiver(broadcastReceiver)
        return true
    }

    companion object {
        private val TAG = NetworkScanner::class.simpleName
        private const val SCAN_TIMEOUT_MS: Long = 30000
    }
}