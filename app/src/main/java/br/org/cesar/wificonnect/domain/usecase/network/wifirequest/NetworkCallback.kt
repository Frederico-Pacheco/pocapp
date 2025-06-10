package br.org.cesar.wificonnect.domain.usecase.network.wifirequest

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


class NetworkCallback(
    private val mCallbackTimeoutInMs: Long = DEFAULT_CALLBACK_TIMEOUT_MS
) : ConnectivityManager.NetworkCallback() {
    private val mOnAvailableBlocker = CountDownLatch(1)
    private val mOnUnAvailableBlocker = CountDownLatch(1)
    private val mOnLostBlocker = CountDownLatch(1)

    private var mOnCapabilitiesChangedBlocker: CountDownLatch? = null

    var network: Network? = null
        private set

    var networkCapabilities: NetworkCapabilities? = null
        private set

    override fun onAvailable(network: Network) {
        Log.v(TAG, "onAvailable")
        this.network = network
        mOnAvailableBlocker.countDown()
    }

    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        Log.v(TAG, "onCapabilitiesChanged")
        this.network = network
        this.networkCapabilities = networkCapabilities
        if (mOnCapabilitiesChangedBlocker != null) mOnCapabilitiesChangedBlocker!!.countDown()
    }

    override fun onUnavailable() {
        Log.v(TAG, "onUnavailable")
        mOnUnAvailableBlocker.countDown()
    }

    override fun onLost(network: Network) {
        Log.v(TAG, "onLost")
        this.network = network
        mOnLostBlocker.countDown()
    }

    fun waitForAvailable(): Pair<Boolean, Network?> {
        if (mOnAvailableBlocker.await(mCallbackTimeoutInMs, TimeUnit.MILLISECONDS)) {
            return Pair(true, network)
        }
        return Pair(false, null)
    }

    fun waitForUnavailable(): Boolean {
        return mOnUnAvailableBlocker.await(mCallbackTimeoutInMs, TimeUnit.MILLISECONDS)
    }

    fun waitForLost(): Boolean {
        return mOnLostBlocker.await(mCallbackTimeoutInMs, TimeUnit.MILLISECONDS)
    }

    fun waitForCapabilitiesChanged(): Boolean {
        mOnCapabilitiesChangedBlocker = CountDownLatch(1)
        return mOnCapabilitiesChangedBlocker!!.await(mCallbackTimeoutInMs, TimeUnit.MILLISECONDS)
    }

    companion object {
        private val TAG: String = NetworkCallback::class.java.simpleName
        private const val DEFAULT_CALLBACK_TIMEOUT_MS = 15000L
    }
}