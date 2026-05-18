package com.example.trackersiklusmenstruasi

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

class BMIGaugeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var bmiValue = 18.0f
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val arcRect = RectF()

    fun setBMI(value: Float) {
        this.bmiValue = value
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        val centerX = w / 2
        val centerY = h * 0.8f
        val radius = w * 0.35f

        arcRect.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 40f
        paint.strokeCap = Paint.Cap.BUTT

        // Draw background arcs
        val colors = intArrayOf(Color.parseColor("#4285F4"), Color.parseColor("#00ACC1"), Color.parseColor("#FBC02D"), Color.parseColor("#388E3C"), Color.parseColor("#1565C0"))
        val sweep = 180f / colors.size
        for (i in colors.indices) {
            paint.color = colors[i]
            canvas.drawArc(arcRect, 180f + (i * sweep), sweep, false, paint)
        }

        // Draw Needle
        val angle = 180f + ((bmiValue - 10f) / 35f * 180f).coerceIn(0f, 180f)
        val radian = Math.toRadians(angle.toDouble())
        val needleLen = radius * 0.8f
        
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
        canvas.drawCircle(centerX, centerY, 15f, paint)
        
        paint.strokeWidth = 8f
        canvas.drawLine(centerX, centerY, 
            (centerX + needleLen * cos(radian)).toFloat(), 
            (centerY + needleLen * sin(radian)).toFloat(), paint)

        // Draw Value Text
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 40f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("%.1f".format(bmiValue), centerX, centerY - 60f, paint)
        paint.textSize = 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("BMI (kg/m²)", centerX, centerY - 30f, paint)
    }
}
