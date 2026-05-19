package com.example.trackersiklusmenstruasi

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class HealthChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    enum class ChartType { BAR, LINE }

    private var chartType = ChartType.BAR
    private var dataPoints = listOf<Float>()
    private var maxValue = 100f
    private var primaryColor = Color.parseColor("#3DDC84") // Default Green
    
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()

    fun setData(data: List<Float>, max: Float, color: Int, type: ChartType) {
        this.dataPoints = data
        this.maxValue = if (max <= 0) 100f else max
        this.primaryColor = color
        this.chartType = type
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (dataPoints.isEmpty()) return

        val w = width.toFloat()
        val h = height.toFloat()
        val padding = 40f
        val chartWidth = w - (padding * 2)
        val chartHeight = h - (padding * 2)
        val stepX = chartWidth / (dataPoints.size - 1).coerceAtLeast(1)

        if (chartType == ChartType.BAR) {
            drawBarChart(canvas, padding, h - padding, chartHeight, stepX)
        } else {
            drawLineChart(canvas, padding, h - padding, chartHeight, stepX)
        }
    }

    private fun drawBarChart(canvas: Canvas, startX: Float, baseY: Float, chartHeight: Float, stepX: Float) {
        val barWidth = stepX * 0.6f
        dataPoints.forEachIndexed { index, value ->
            val barHeight = (value / maxValue) * chartHeight
            val x = startX + (index * stepX)
            
            // Draw background bar (light)
            paint.color = primaryColor
            paint.alpha = 40
            canvas.drawRoundRect(x - barWidth/2, baseY - chartHeight, x + barWidth/2, baseY, 12f, 12f, paint)
            
            // Draw actual value bar
            paint.alpha = 255
            canvas.drawRoundRect(x - barWidth/2, baseY - barHeight, x + barWidth/2, baseY, 12f, 12f, paint)
            
            // Highlight specific bar (e.g., the tallest one in the screenshot)
            if (value == dataPoints.maxOrNull()) {
                paint.color = Color.WHITE
                canvas.drawCircle(x, baseY - barHeight + 10f, 6f, paint)
                paint.color = primaryColor
            }
        }
    }

    private fun drawLineChart(canvas: Canvas, startX: Float, baseY: Float, chartHeight: Float, stepX: Float) {
        path.reset()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 6f
        paint.color = primaryColor
        paint.alpha = 255

        dataPoints.forEachIndexed { index, value ->
            val x = startX + (index * stepX)
            val y = baseY - (value / maxValue) * chartHeight
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        canvas.drawPath(path, paint)

        // Draw area under line
        path.lineTo(startX + (dataPoints.size - 1) * stepX, baseY)
        path.lineTo(startX, baseY)
        path.close()
        paint.style = Paint.Style.FILL
        paint.alpha = 30
        canvas.drawPath(path, paint)
        
        // Draw points
        paint.alpha = 255
        dataPoints.forEachIndexed { index, value ->
            val x = startX + (index * stepX)
            val y = baseY - (value / maxValue) * chartHeight
            canvas.drawCircle(x, y, 8f, paint)
            paint.color = Color.WHITE
            canvas.drawCircle(x, y, 4f, paint)
            paint.color = primaryColor
        }
    }
}
