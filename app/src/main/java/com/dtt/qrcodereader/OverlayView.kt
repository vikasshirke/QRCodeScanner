package com.dtt.qrcodereader


import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class OverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val backgroundPaint = Paint().apply {
        color = 0xCC000000.toInt()
        style = Paint.Style.FILL
    }

    private val borderPaint = Paint().apply {
        color = 0xFFFFFFFF.toInt()
        style = Paint.Style.STROKE
        strokeWidth = 6f
        isAntiAlias = true
    }

    private val cornerPaint = Paint().apply {
        color = 0xFFFFFFFF.toInt()
        style = Paint.Style.STROKE
        strokeWidth = 12f
        isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = canvas.width
        val height = canvas.height

        val squareSize = (width * 0.6).coerceAtMost(height * 0.6).toFloat() - 0.6f


        val left = (width - squareSize) / 2
        val top = (height - squareSize) / 2
        val right = left + squareSize
        val bottom = top + squareSize
        val cornerRadius = 24f


        val overlayPath = Path().apply {
            addRect(0f, 0f, width.toFloat(), height.toFloat(), Path.Direction.CW)
            addRoundRect(left, top, right, bottom, cornerRadius, cornerRadius, Path.Direction.CCW)
        }

        canvas.drawPath(overlayPath, backgroundPaint)

        canvas.drawRoundRect(left, top, right, bottom, cornerRadius, cornerRadius, borderPaint)

//        val cornerLength = 40f
//        drawCorners(canvas, left, top, right, bottom, cornerLength)
    }

    private fun drawCorners(
        canvas: Canvas,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        length: Float
    ) {
        // Top-left corner
        canvas.drawLine(left, top, left + length, top, cornerPaint)
        canvas.drawLine(left, top, left, top + length, cornerPaint)

        // Top-right corner
        canvas.drawLine(right, top, right - length, top, cornerPaint)
        canvas.drawLine(right, top, right, top + length, cornerPaint)

        // Bottom-left corner
        canvas.drawLine(left, bottom, left + length, bottom, cornerPaint)
        canvas.drawLine(left, bottom, left, bottom - length, cornerPaint)

        // Bottom-right corner
        canvas.drawLine(right, bottom, right - length, bottom, cornerPaint)
        canvas.drawLine(right, bottom, right, bottom - length, cornerPaint)
    }
}




