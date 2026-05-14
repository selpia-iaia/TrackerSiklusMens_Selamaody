package com.example.trackersiklusmenstruasi

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

class ArcRulerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var currentValue: Int = 57
    private val pinkColor = Color.parseColor("#FF5C7A")
    private val blueColor = Color.parseColor("#4285F4")
    private val softGray = Color.parseColor("#E0E0E0")

    fun setValue(value: Int) {
        currentValue = value
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height.toFloat() * 1.8f // Move center further down
        val topOfArcY = 60f
        val radius = centerY - topOfArcY

        // Draw Arc Ruler Lines
        for (i in (currentValue - 40)..(currentValue + 40)) {
            val angle = 270f + (i - currentValue) * 2.5f
            
            // Limit drawing range to visible area (left and right sides)
            if (angle < 225f || angle > 315f) continue

            val radian = Math.toRadians(angle.toDouble())
            
            val startX = centerX + radius * cos(radian).toFloat()
            val startY = centerY + radius * sin(radian).toFloat()
            
            val isMajor = i % 10 == 0
            val lineLength = if (isMajor) 50f else 25f
            
            val endX = centerX + (radius - lineLength) * cos(radian).toFloat()
            val endY = centerY + (radius - lineLength) * sin(radian).toFloat()

            paint.strokeWidth = if (isMajor) 5f else 3f
            
            // Coloring based on position as in reference screenshot
            paint.color = when {
                i < 50 -> blueColor
                i > 70 -> pinkColor
                else -> Color.parseColor("#808080") // Gray for middle values
            }
            if (!isMajor) paint.color = softGray

            canvas.drawLine(startX, startY, endX, endY, paint)

            // Draw numbers below major lines
            if (isMajor) {
                paint.color = Color.BLACK
                paint.textSize = 34f
                paint.textAlign = Paint.Align.CENTER
                paint.style = Paint.Style.FILL
                paint.typeface = Typeface.DEFAULT_BOLD
                
                // Position text slightly below the lines
                val textRadius = radius - lineLength - 35f
                val textX = centerX + textRadius * cos(radian).toFloat()
                val textY = centerY + textRadius * sin(radian).toFloat()
                canvas.drawText(i.toString(), textX, textY, paint)
            }
        }

        // Draw center indicator (Vertical pink line with dots)
        paint.color = pinkColor
        paint.strokeWidth = 6f
        // Line from top to slightly past the arc top
        canvas.drawLine(centerX, 0f, centerX, topOfArcY + 40f, paint)
        
        paint.style = Paint.Style.FILL
        canvas.drawCircle(centerX, 0f, 10f, paint)
        canvas.drawCircle(centerX, topOfArcY + 40f, 12f, paint)
    }
}
