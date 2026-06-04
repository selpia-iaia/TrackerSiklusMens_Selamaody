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
        textSize = 40f
        isFakeBoldText = true
    }

    private val needlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        strokeWidth = 8f
        strokeCap = Paint.Cap.ROUND
    }

    private var bmiValue: Float = 19.0f
    
    // Colors from design: Blue, Green, Yellow, Orange
    private val colors = intArrayOf(
        Color.parseColor("#4FC3F7"), // Blue (Underweight)
        Color.parseColor("#4CAF50"), // Green (Normal)
        Color.parseColor("#FFEB3B"), // Yellow (Overweight)
        Color.parseColor("#FB8C00")  // Orange (Obese)
    )

    fun setBMI(value: Float) {
        bmiValue = value
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height * 0.85f
        val radius = width * 0.4f
        val thickness = 50f
        
        paint.strokeWidth = thickness
        val rectF = RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius)

        // Draw 4 segments (180 degrees total)
        val sweepAngle = 180f / 4f
        for (i in 0 until 4) {
            paint.color = colors[i]
            // Draw arcs with a small gap (2 degrees)
            canvas.drawArc(rectF, 180f + (i * sweepAngle) + 1f, sweepAngle - 2f, false, paint)
        }

        // Draw BMI Text in center
        canvas.drawText(String.format("%.1f", bmiValue), centerX, centerY - 60f, textPaint)
        
        val labelPaint = Paint(textPaint).apply { textSize = 24f; color = Color.GRAY; isFakeBoldText = false }
        canvas.drawText("BMI (kg/m²)", centerX, centerY - 100f, labelPaint)

        // Draw Needle
        // Map BMI 10-40 to 180-360 degrees
        val bmiRange = 40f - 10f
        val angle = 180f + ((bmiValue - 10f) / bmiRange) * 180f
        val needleLength = radius - 20f
        
        val stopX = centerX + Math.cos(Math.toRadians(angle.toDouble())).toFloat() * needleLength
        val stopY = centerY + Math.sin(Math.toRadians(angle.toDouble())).toFloat() * needleLength
        
        canvas.drawLine(centerX, centerY, stopX, stopY, needlePaint)
        
        // Needle base circle
        canvas.drawCircle(centerX, centerY, 15f, needlePaint)
    }
}
