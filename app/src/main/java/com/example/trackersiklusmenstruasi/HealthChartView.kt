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
    private var highlightIndex = -1
    private var unit = ""

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()

    fun setData(data: List<Float>, max: Float, color: Int, type: ChartType, highlightIdx: Int = -1, unitStr: String = "") {
        this.dataPoints = data
        this.maxValue = if (max <= 0) 100f else max
        this.primaryColor = color
        this.chartType = type
        this.highlightIndex = highlightIdx
        this.unit = unitStr
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (dataPoints.isEmpty()) return

        val w = width.toFloat()
        val h = height.toFloat()
        val paddingHorizontal = 60f
        val paddingTop = 100f
        val paddingBottom = 40f
        
        val chartWidth = w - (paddingHorizontal * 2)
        val chartHeight = h - paddingTop - paddingBottom
        val stepX = chartWidth / (dataPoints.size - 1).coerceAtLeast(1)
        val baseY = h - paddingBottom

        if (chartType == ChartType.BAR) {
            drawBarChart(canvas, paddingHorizontal, baseY, chartHeight, stepX)
        } else {
            drawLineChart(canvas, paddingHorizontal, baseY, chartHeight, stepX)
        }
    }

    private fun drawBarChart(canvas: Canvas, startX: Float, baseY: Float, chartHeight: Float, stepX: Float) {
        val barWidth = stepX * 0.4f
        dataPoints.forEachIndexed { index, value ->
            val barHeight = (value / maxValue) * chartHeight
            val x = startX + (index * stepX)
            
            // Draw background full-height bar (very light)
            paint.style = Paint.Style.FILL
            paint.color = primaryColor
            paint.alpha = 20
            canvas.drawRoundRect(x - barWidth/2, baseY - chartHeight, x + barWidth/2, baseY, 8f, 8f, paint)
            
            // Draw actual value bar
            paint.alpha = if (index == highlightIndex) 255 else 100
            canvas.drawRoundRect(x - barWidth/2, baseY - barHeight, x + barWidth/2, baseY, 8f, 8f, paint)
            
            if (index == highlightIndex) {
                drawHighlight(canvas, x, baseY - barHeight, value)
            }
        }
    }

    private fun drawLineChart(canvas: Canvas, startX: Float, baseY: Float, chartHeight: Float, stepX: Float) {
        path.reset()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f
        paint.color = primaryColor
        paint.alpha = 255

        dataPoints.forEachIndexed { index, value ->
            val x = startX + (index * stepX)
            val y = baseY - (value / maxValue) * chartHeight
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        canvas.drawPath(path, paint)

        // Fill area
        val fillPath = Path(path)
        fillPath.lineTo(startX + (dataPoints.size - 1) * stepX, baseY)
        fillPath.lineTo(startX, baseY)
        fillPath.close()
        paint.style = Paint.Style.FILL
        paint.alpha = 40
        canvas.drawPath(fillPath, paint)
        
        // Points
        dataPoints.forEachIndexed { index, value ->
            val x = startX + (index * stepX)
            val y = baseY - (value / maxValue) * chartHeight
            
            paint.style = Paint.Style.FILL
            paint.alpha = 255
            paint.color = primaryColor
            canvas.drawCircle(x, y, 10f, paint)
            paint.color = Color.WHITE
            canvas.drawCircle(x, y, 6f, paint)
            
            if (index == highlightIndex) {
                drawHighlight(canvas, x, y, value)
            }
        }
    }

    private fun drawHighlight(canvas: Canvas, x: Float, y: Float, value: Float) {
        paint.style = Paint.Style.FILL
        paint.color = Color.BLACK
        paint.alpha = 255
        paint.textSize = 36f
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textAlign = Paint.Align.CENTER
        
        val text = "${value.toInt()}$unit"
        val textBounds = Rect()
        paint.getTextBounds(text, 0, text.length, textBounds)
        
        // Draw small dot on the bar/line point
        paint.color = Color.WHITE
        canvas.drawCircle(x, y, 6f, paint)
        
        // Draw value text above
        paint.color = Color.BLACK
        canvas.drawText(text, x, y - 40f, paint)
        
        // Draw horizontal dashed line if needed (from screenshot)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        paint.color = Color.LTGRAY
        paint.pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
        canvas.drawLine(0f, y, width.toFloat(), y, paint)
        paint.pathEffect = null
    }
}
