package br.org.cesar.wificonnect.domain.usecase.network.mobilesignal;

import android.telephony.CellSignalStrengthLte
import android.telephony.SignalStrength
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.util.Log
import br.org.cesar.wificonnect.domain.usecase.CsvUseCase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

class NetworkSignalUseCase @Inject constructor(
    private val telephonyManager: TelephonyManager,
    private val csvUseCase: CsvUseCase
) : TelephonyCallback(), TelephonyCallback.SignalStrengthsListener {
    private var backgroundExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())

    override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
        Log.d(TAG, "level: ${signalStrength.level}")
        for (cellSignalStrength in signalStrength.cellSignalStrengths) {
            if (cellSignalStrength is CellSignalStrengthLte) {
                logSignalStrength(cellSignalStrength)
            }
        }
    }

    fun registerSignalStrengthListener() {
        if (backgroundExecutor.isShutdown) {
            backgroundExecutor = Executors.newSingleThreadExecutor()
        }

        telephonyManager.registerTelephonyCallback(backgroundExecutor, this)
    }

    fun unregisterSignalStrengthListener() {
        telephonyManager.unregisterTelephonyCallback(this)
        backgroundExecutor.execute {
            csvUseCase.closeCurrentStreamAndFinalize()
            Log.i(TAG, "Requested to start a new log file. Current one closed.")
        }
    }

    fun cleanup() {
        if (!backgroundExecutor.isShutdown) {
            backgroundExecutor.shutdown()
            Log.d(TAG, "Background executor shut down.")
        }
    }

    private fun logSignalStrength(signalStrength: CellSignalStrengthLte) {
        val fileName = "signal_strength"
        val csvHeader = "Timestamp,RSRP,RSRQ,RSSI\n"

        val logEntry = "${dateFormat.format(Date())}," +
                "${signalStrength.rsrp}," +
                "${signalStrength.rsrq}," +
                "${signalStrength.rssi}\n"

        backgroundExecutor.execute {
            csvUseCase.ensureStreamAndWriteHeader(fileName, csvHeader)
            csvUseCase.currentOutputStream?.let { stream ->
                csvUseCase.writeToStream(stream, logEntry)
            } ?: Log.e(TAG, "OutputStream is null, cannot write log entry.")
        }

        Log.d(TAG, "RSRP: ${signalStrength.rsrp} dBm")
        Log.d(TAG, "RSRQ: ${signalStrength.rsrq} dB")
        Log.d(TAG, "RSSI: ${signalStrength.rssi} dBm")
    }

    companion object {
        private val TAG = NetworkSignalUseCase::class.java.simpleName
    }
}
