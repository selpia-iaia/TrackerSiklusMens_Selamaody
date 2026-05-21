package com.example.trackersiklusmenstruasi

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class CycleProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var progress: Float = 0.35f 
    private val pinkColor = Color.parseColor("#FF5C7A") 
    private val lightGrayColor = Color.parseColor("#F5F5F5")
    private val whiteColor = Color.WHITE

    fun setProgress(newProgress: Float) {
        progress = newProgress.coerceIn(0f, 1f)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val strokeWidth = 55f
        val radius = (width.coerceAtMost(height) - strokeWidth) / 2f

        // 1. Draw Gray Background Circle
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        paint.color = lightGrayColor
        paint.strokeCap = Paint.Cap.ROUND
        canvas.drawCircle(centerX, centerY, radius, paint)

        // 2. Draw Pink Progress Arc
        paint.color = pinkColor
        val rectF = RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
        canvas.drawArc(rectF, -90f, progress * 360f, false, paint)

        // 3. Draw the white circular thumb at the end of progress
        paint.style = Paint.Style.FILL
        paint.color = whiteColor
        
        val angle = Math.toRadians((progress * 360.0 - 90.0))
        val dotX = centerX + radius * Math.cos(angle).toFloat()
        val dotY = centerY + radius * Math.sin(angle).toFloat()
        
        // Add shadow for the thumb
        paint.setShadowLayer(8f, 0f, 2f, Color.parseColor("#30000000"))
        canvas.drawCircle(dotX, dotY, 24f, paint)
        paint.clearShadowLayer()
    }
}
