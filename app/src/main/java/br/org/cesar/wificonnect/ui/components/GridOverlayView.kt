package br.org.cesar.wificonnect.ui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

class GridOverlayView(context: Context) : View(context) {
    private val paint = Paint().apply {
        color = Color.argb(100, 0, 255, 0)
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val spacingX = width * 0.05f // 5% da largura
        val spacingY = height * 0.05f // 5% da altura

        // Linhas verticais
        var x = spacingX
        while (x < width) {
            canvas.drawLine(x, 0f, x, height.toFloat(), paint)
            x += spacingX
        }

        // Linhas horizontais
        var y = spacingY
        while (y < height) {
            canvas.drawLine(0f, y, width.toFloat(), y, paint)
            y += spacingY
        }
    }
}