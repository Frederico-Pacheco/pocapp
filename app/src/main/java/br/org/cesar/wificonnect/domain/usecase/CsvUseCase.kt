package br.org.cesar.wificonnect.domain.usecase

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CsvUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fileNameDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    private var isHeaderWritten = false
    private var currentCsvUri: Uri? = null
    var currentOutputStream: OutputStream? = null

    fun ensureStreamAndWriteHeader(fileName: String, csvHeader: String) {
        if (currentOutputStream == null) {
            currentOutputStream = createCsvFileAndGetStream(fileName)
        }
        currentOutputStream?.let { stream ->
            if (!isHeaderWritten) {
                writeToStream(stream, csvHeader, true)
            }
        }
    }

    fun writeToStream(outputStream: OutputStream, data: String, isHeader: Boolean = false) {
        try {
            outputStream.write(data.toByteArray())
            if (!isHeader) outputStream.flush()
            if (isHeader) isHeaderWritten = true
        } catch (e: IOException) {
            Log.e(TAG, "Error writing to CSV output stream", e)
            closeCurrentStreamAndFinalize()
        }
    }

    fun closeCurrentStreamAndFinalize() {
        currentOutputStream?.let { stream ->
            try {
                stream.flush()
                stream.close()
                Log.d(TAG, "CSV output stream closed.")
            } catch (e: IOException) {
                Log.e(TAG, "Error closing CSV output stream", e)
            }
        }
        currentOutputStream = null

        currentCsvUri?.let { uri ->
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.IS_PENDING, 0)
            }

            try {
                context.contentResolver.update(uri, contentValues, null, null)
                Log.i(TAG, "CSV file finalized (IS_PENDING set to 0): $uri")
            } catch (e: Exception) {
                Log.e(TAG, "Error finalizing MediaStore entry", e)
            }
        }
        currentCsvUri = null
        isHeaderWritten = false
    }

    private fun createCsvFileAndGetStream(fileName: String): OutputStream? {
        val timestamp = fileNameDateFormat.format(Date())
        val fileFullName = "${fileName}_log_${timestamp}.csv"

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileFullName)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }

        val resolver = context.contentResolver
        var newUri: Uri? = null
        try {
            newUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            if (newUri == null) {
                Log.e(TAG, "Failed to create new MediaStore entry for CSV.")
                return null
            }

            currentCsvUri = newUri
            isHeaderWritten = false
            Log.i(TAG, "CSV file created at: $newUri")

            return resolver.openOutputStream(newUri)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating CSV file in MediaStore", e)

            newUri?.let {
                try {
                    resolver.delete(it, null, null)
                } catch (deleteEx: Exception) {
                    Log.e(TAG, "Error cleaning up partially created MediaStore entry", deleteEx)
                }
            }
            currentCsvUri = null

            return null
        }
    }

    companion object {
        private val TAG = CsvUseCase::class.java.simpleName
    }
}