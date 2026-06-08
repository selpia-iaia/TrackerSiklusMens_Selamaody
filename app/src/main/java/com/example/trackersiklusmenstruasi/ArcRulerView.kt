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
    private val textGray = Color.parseColor("#808080")

    private var minValue = 0
    private var maxValue = 200

    fun setValue(value: Int) {
        currentValue = value
        invalidate()
    }

    fun setRange(min: Int, max: Int) {
        this.minValue = min
        this.maxValue = max
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height.toFloat() * 1.6f // Push center further down for flatter arc
        val topOfArcY = 40f
        val radius = centerY - topOfArcY

        val range = 40 // Show more visible ticks
        for (i in (currentValue - range)..(currentValue + range)) {
            // Adjust angle spacing to match design spacing
            val angle = 270f + (i - currentValue) * 2.5f
            
            if (angle < 225f || angle > 315f) continue
            if (i < minValue || i > maxValue) continue

            val radian = Math.toRadians(angle.toDouble())
            
            val startX = centerX + radius * cos(radian).toFloat()
            val startY = centerY + radius * sin(radian).toFloat()
            
            val isMajor = i % 10 == 0 || (i == 40 || i == 84 || i == 74) // Match specific labels in image 34
            val lineLength = if (isMajor) 45f else 25f
            
            val endX = centerX + (radius - lineLength) * cos(radian).toFloat()
            val endY = centerY + (radius - lineLength) * sin(radian).toFloat()

            paint.strokeWidth = if (isMajor) 3f else 1.5f
            
            // Subtle color transition like image
            paint.color = when {
                i < currentValue - 10 -> blueColor
                i > currentValue + 10 -> pinkColor
                else -> textGray
            }
            if (!isMajor) paint.alpha = 80

            canvas.drawLine(startX, startY, endX, endY, paint)

            if (isMajor) {
                paint.alpha = 255
                paint.color = Color.BLACK
                paint.textSize = 34f
                paint.textAlign = Paint.Align.CENTER
                paint.style = Paint.Style.FILL
                paint.typeface = Typeface.DEFAULT_BOLD
                
                // Numbers positioned below the arc ticks
                val textRadius = radius - lineLength - 35f
                val textX = centerX + textRadius * cos(radian).toFloat()
                val textY = centerY + textRadius * sin(radian).toFloat()
                canvas.drawText(i.toString(), textX, textY + 10f, paint)
            }
        }

        // Draw Center Indicator Sesuai Gambar
        paint.color = pinkColor
        paint.strokeWidth = 4f
        
        // Vertical line with dots
        val lineStartY = topOfArcY - 30f
        val lineEndY = topOfArcY + 40f
        canvas.drawLine(centerX, lineStartY, centerX, lineEndY, paint)
        
        paint.style = Paint.Style.FILL
        // Small dot at top
        canvas.drawCircle(centerX, lineStartY, 6f, paint)
        // Larger circle where it points to the arc
        canvas.drawCircle(centerX, lineEndY, 8f, paint)
    }
}
