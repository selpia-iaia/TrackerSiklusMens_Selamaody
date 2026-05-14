package com.example.trackersiklusmenstruasi

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class WaterGaugeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var progress: Float = 0.51f // 1230 / 2400
    private val blueColor = Color.parseColor("#3DA9FF")
    private val grayColor = Color.parseColor("#F0F0F0")
    
    fun setProgress(progress: Float) {
        this.progress = progress.coerceIn(0f, 1f)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val strokeWidth = 45f
        val radius = (width.coerceAtMost(height) - strokeWidth) / 2f - 40f

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        paint.strokeCap = Paint.Cap.ROUND

        val rectF = RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius)

        // Draw Background Arc (270 degrees)
        paint.color = grayColor
        canvas.drawArc(rectF, 135f, 270f, false, paint)

        // Draw Progress Arc
        paint.color = blueColor
        canvas.drawArc(rectF, 135f, 270f * progress, false, paint)
        
        // Draw Ticks inside
        paint.strokeWidth = 5f
        paint.color = Color.parseColor("#E0E0E0")
        val tickCount = 9
        val tickLength = 15f
        val tickMargin = 25f
        val innerRadius = radius - strokeWidth/2 - tickMargin
        
        for (i in 0 until tickCount) {
            val angle = 135f + (270f / (tickCount - 1)) * i
            val rad = Math.toRadians(angle.toDouble())
            val startX = centerX + (innerRadius - tickLength) * Math.cos(rad).toFloat()
            val startY = centerY + (innerRadius - tickLength) * Math.sin(rad).toFloat()
            val stopX = centerX + innerRadius * Math.cos(rad).toFloat()
            val stopY = centerY + innerRadius * Math.sin(rad).toFloat()
            canvas.drawLine(startX, startY, stopX, stopY, paint)
        }
    }
}
