package br.org.cesar.wificonnect.domain.usecase.network.wifirequest

import android.net.wifi.ScanResult
import android.os.Build

fun ScanResult.getSsid(): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this.wifiSsid.toString().replace("\"", "")
    } else {
        this.SSID
    }
}