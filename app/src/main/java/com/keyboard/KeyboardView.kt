package com.keyboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View

class KeyboardView(context: Context) : View(context) {

    var onKeyPress: ((String) -> Unit)? = null
    var onSwipeDown: (() -> Unit)? = null

    private var isShiftOn = false
    private var isCtrlOn = false

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val keys = listOf(
        listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p"),
        listOf("a", "s", "d", "f", "g", "h", "j", "k", "l"),
        listOf("SHIFT", "z", "x", "c", "v", "b", "n", "m", "DEL"),
        listOf("CTRL", "SPACE", "ENTER")
    )

    private data class KeyRect(val key: String, val rect: RectF)
    private val keyRects = mutableListOf<KeyRect>()

    private var touchStartX = 0f
    private var touchStartY = 0f
    private var activeKey: KeyRect? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calculateKeyRects(w, h)
    }

    private fun calculateKeyRects(w: Int, h: Int) {
        keyRects.clear()
        val rowHeight = h.toFloat() / keys.size
        val padding = 8f

        keys.forEachIndexed { rowIndex, row ->
            val keyWidth = w.toFloat() / row.size
            val top = rowIndex * rowHeight + padding
            val bottom = top + rowHeight - padding * 2

            row.forEachIndexed { colIndex, key ->
                val left = colIndex * keyWidth + padding
                val right = left + keyWidth - padding * 2

                val adjustedRight = when (key) {
                    "SPACE" -> left + keyWidth * 3 - padding * 2
                    else -> right
                }

                keyRects.add(KeyRect(key, RectF(left, top, adjustedRight, bottom)))
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.parseColor("#1a1a2e"))

        keyRects.forEach { keyRect ->
            val isModifierActive = (keyRect.key == "SHIFT" && isShiftOn) ||
                    (keyRect.key == "CTRL" && isCtrlOn)

            paint.color = when {
                isModifierActive -> Color.parseColor("#e94560")
                keyRect.key in listOf("SHIFT", "CTRL", "DEL", "ENTER") ->
                    Color.parseColor("#16213e")
                keyRect.key == "SPACE" -> Color.parseColor("#16213e")
                else -> Color.parseColor("#0f3460")
            }

            canvas.drawRoundRect(keyRect.rect, 12f, 12f, paint)

            paint.color = Color.WHITE
            paint.textSize = when (keyRect.key) {
                "SPACE", "SHIFT", "CTRL", "DEL", "ENTER" -> 28f
                else -> 36f
            }
            paint.textAlign = Paint.Align.CENTER

            val label = when (keyRect.key) {
                "SPACE" -> "SPACE"
                "DEL" -> "⌫"
                "ENTER" -> "↵"
                else -> if (isShiftOn) keyRect.key.uppercase() else keyRect.key
            }

            val textY = keyRect.rect.centerY() - (paint.descent() + paint.ascent()) / 2
            canvas.drawText(label, keyRect.rect.centerX(), textY, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStartX = event.x
                touchStartY = event.y
                activeKey = keyRects.find { it.rect.contains(event.x, event.y) }
            }
            MotionEvent.ACTION_UP -> {
                val deltaY = event.y - touchStartY
                val deltaX = event.x - touchStartX

                if (deltaY > 50 && Math.abs(deltaX) < 100) {
                    val swipedKey = keyRects.find { it.rect.contains(touchStartX, touchStartY) }
                    if (swipedKey != null && swipedKey.key.length == 1) {
                        onSwipeDown?.invoke()
                        invalidate()
                        return true
                    }
                }

                activeKey?.let { onKeyPress?.invoke(it.key) }
                invalidate()
            }
        }
        return true
    }

    fun updateModifierState(shift: Boolean, ctrl: Boolean) {
        isShiftOn = shift
        isCtrlOn = ctrl
        invalidate()
    }
}