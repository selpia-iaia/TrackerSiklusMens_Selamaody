package com.example.trackersiklusmenstruasi

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class BMIGaugeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.BUTT
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        color = Color.BLACK
        textSize = 44f
        typeface = Typeface.DEFAULT_BOLD
    }

    private val needlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        strokeWidth = 10f
        strokeCap = Paint.Cap.ROUND
    }

    private var bmiValue: Float = 18.0f
    
    // 6 Colors matching the legend in screenshot
    private val colors = intArrayOf(
        Color.parseColor("#4FC3F7"), // Blue
        Color.parseColor("#4DB6AC"), // Teal
        Color.parseColor("#4CAF50"), // Green
        Color.parseColor("#FFEB3B"), // Yellow
        Color.parseColor("#FB8C00"), // Orange
        Color.parseColor("#F44336")  // Red
    )

    fun setBMI(value: Float) {
        bmiValue = value
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height * 0.8f
        val radius = width * 0.35f
        val thickness = 40f
        
        paint.strokeWidth = thickness
        val rectF = RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius)

        // Draw 6 segments (180 degrees total)
        val sweepAngle = 180f / 6f
        for (i in 0 until 6) {
            paint.color = colors[i]
            // Arcs with gaps
            canvas.drawArc(rectF, 180f + (i * sweepAngle) + 2f, sweepAngle - 4f, false, paint)
        }

        // Draw BMI Text
        canvas.drawText(String.format("%.1f", bmiValue), centerX, centerY - 60f, textPaint)
        
        val labelPaint = Paint(textPaint).apply { 
            textSize = 20f 
            color = Color.GRAY 
            typeface = Typeface.DEFAULT
        }
        canvas.drawText("BMI (kg/m²)", centerX, centerY - 100f, labelPaint)

        // Draw Needle
        // Map BMI (range 10 to 40) to 180 to 360 degrees
        val normalizedBMI = (bmiValue - 10f).coerceIn(0f, 30f)
        val angle = 180f + (normalizedBMI / 30f) * 180f
        val needleLength = radius - 30f
        
        val stopX = centerX + Math.cos(Math.toRadians(angle.toDouble())).toFloat() * needleLength
        val stopY = centerY + Math.sin(Math.toRadians(angle.toDouble())).toFloat() * needleLength
        
        canvas.drawLine(centerX, centerY, stopX, stopY, needlePaint)
        canvas.drawCircle(centerX, centerY, 18f, needlePaint)
        
        // Inner center highlight
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        canvas.drawCircle(centerX, centerY, 6f, paint)
    }
}
