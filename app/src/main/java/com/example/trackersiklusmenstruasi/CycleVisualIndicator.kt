package com.example.trackersiklusmenstruasi

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CycleVisualIndicator @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val dashPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 10f
        strokeCap = Paint.Cap.ROUND
    }

    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#FF5C7A") // Pink main
    }

    private var duration: Int = 20
    private var maxDuration: Int = 35

    fun setData(duration: Int, maxDuration: Int = 35) {
        this.duration = duration
        this.maxDuration = maxDuration
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height / 2f
        val segmentCount = 10
        val segmentGap = 15f
        val segmentWidth = (width - (segmentCount - 1) * segmentGap) / segmentCount

        val pinkSegments = (duration.toFloat() / maxDuration * segmentCount).toInt().coerceAtLeast(1)

        for (i in 0 until segmentCount) {
            val startX = i * (segmentWidth + segmentGap)
            val stopX = startX + segmentWidth
            
            if (i < pinkSegments) {
                dashPaint.color = Color.parseColor("#FF5C7A")
            } else {
                dashPaint.color = Color.parseColor("#F0F0F0")
            }
            
            canvas.drawLine(startX, height, stopX, height, dashPaint)
            
            // Draw circle at the end of pink segments
            if (i == pinkSegments - 1) {
                canvas.drawCircle(stopX, height, 12f, circlePaint)
            }
        }
    }
}
