package br.org.cesar.wificonnect.domain.usecase

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import br.org.cesar.wificonnect.ui.components.GridOverlayView
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class OverlayUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mWindowManager: WindowManager,
) {
    private var gestureOverlayView: View? = null

    fun showClickIndicator(x: Float, y: Float) {
        if (gestureOverlayView != null) {
            mWindowManager.removeView(gestureOverlayView)
        }

        gestureOverlayView = View(context).apply {
            setBackgroundColor(Color.argb(128, 255, 0, 0)) // Quadrado vermelho semitransparente
        }

        val params = WindowManager.LayoutParams(
            50, // largura pequena
            50, // altura pequena
            (x - 25).toInt(), // centralizar no X
            (y - 25).toInt(), // centralizar no Y
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, // Requer permissão SYSTEM_ALERT_WINDOW
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
        }

        try {
            mWindowManager.addView(gestureOverlayView, params)

            Handler(Looper.getMainLooper()).postDelayed({
                hideClickIndicator()
            }, 1000)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao adicionar overlay", e)
        }
    }

    fun showGridOverlay() {
        val overlayView = GridOverlayView(context)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
        }

        try {
            mWindowManager.addView(overlayView, params)

            Handler(Looper.getMainLooper()).postDelayed({
                mWindowManager.removeView(overlayView)
            }, 1000)
        } catch (e: Exception) {
            Log.e("Overlay", "Erro ao adicionar grid overlay", e)
        }
    }

    private fun hideClickIndicator() {
        gestureOverlayView?.let {
            try {
                mWindowManager.removeView(it)
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao remover overlay", e)
            }
            gestureOverlayView = null
        }
    }

    companion object {
        private val TAG = OverlayUseCase::class.java.simpleName
    }
}