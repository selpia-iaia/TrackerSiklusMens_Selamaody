package com.example.trackersiklusmenstruasi

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class WaterGaugeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var progress: Float = 0.51f 
    private val blueColor = Color.parseColor("#3B82F6") 
    private val grayColor = Color.parseColor("#F3F4F6") 
    
    fun setProgress(progress: Float) {
        this.progress = progress.coerceIn(0f, 1f)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        
        // Buat jauh lebih besar dan tebal sesuai permintaan "kurang gde"
        val strokeWidth = 70f 
        val radius = (width.coerceAtMost(height) - strokeWidth) / 2f - 10f

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        paint.strokeCap = Paint.Cap.ROUND

        val rectF = RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius)

        // 1. Draw Background Track (Sangat tebal & warna lembut)
        paint.color = grayColor
        canvas.drawArc(rectF, 135f, 270f, false, paint)

        // 2. Draw Progress (Warna biru vibran)
        paint.color = blueColor
        canvas.drawArc(rectF, 135f, 270f * progress, false, paint)
        
        // 3. Draw Interior Ticks (Titik-titik halus di dalam)
        paint.strokeWidth = 3f
        paint.color = Color.parseColor("#E5E7EB")
        val tickCount = 13
        val tickLength = 12f
        val tickMargin = 40f
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
