package com.example.trackersiklusmenstruasi

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class CycleProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val thumbPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.WHITE
    }

    private val thumbStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 6f
        color = Color.parseColor("#FF5C7A") 
    }

    private var progress: Float = 0.35f 
    private val startColor = Color.parseColor("#FFDDE1") 
    private val endColor = Color.parseColor("#FF5C7A") 

    fun setProgress(newProgress: Float) {
        progress = newProgress.coerceIn(0f, 1f)
        invalidate()
    }

    fun setProgress(newProgress: Float) {
        progress = newProgress.coerceIn(0f, 1f)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (width == 0 || height == 0) return

        val centerX = width / 2f
        val centerY = height / 2f
        val size = width.coerceAtMost(height).toFloat()
        
        // "Tebal": set stroke width as 15% of the total size
        val strokeWidthValue = size * 0.15f
        backgroundPaint.strokeWidth = strokeWidthValue
        backgroundPaint.color = Color.parseColor("#F5F5F5")
        
        progressPaint.strokeWidth = strokeWidthValue

        // "Agak kecil": radius is slightly smaller to leave margin but not hidden by text
        val radius = (size - strokeWidthValue - 60f) / 2f
        
        if (radius <= 0) return

        // 1. Draw Background Track
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint)

        // 2. Draw Progress Arc with Gradient
        val rectF = RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
        
        val gradient = SweepGradient(centerX, centerY, 
            intArrayOf(startColor, endColor, endColor), 
            floatArrayOf(0f, progress, 1f)
        )
        val matrix = Matrix()
        matrix.postRotate(-90f, centerX, centerY)
        gradient.setLocalMatrix(matrix)
        
        progressPaint.shader = gradient
        canvas.drawArc(rectF, -90f, progress * 360f, false, progressPaint)

        // 3. Draw the circular thumb
        val angle = Math.toRadians((progress * 360.0 - 90.0))
        val dotX = centerX + radius * Math.cos(angle).toFloat()
        val dotY = centerY + radius * Math.sin(angle).toFloat()
        
        val thumbRadius = strokeWidthValue / 2f - 2f
        
        thumbPaint.setShadowLayer(10f, 0f, 4f, Color.parseColor("#30000000"))
        canvas.drawCircle(dotX, dotY, thumbRadius, thumbPaint)
        thumbPaint.clearShadowLayer()
        
        canvas.drawCircle(dotX, dotY, thumbRadius, thumbStrokePaint)
    }
}
