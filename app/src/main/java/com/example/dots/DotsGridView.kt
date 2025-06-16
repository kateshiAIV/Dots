package com.example.dots

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DotsGridView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    val cellSize = 40f
    val dotRadius = 8f

    private val gridPaint = Paint().apply {
        color = Color.LTGRAY
        strokeWidth = 2f
    }

    private val dotPaint = Paint().apply {
        style = Paint.Style.FILL
    }

    // Список точек с координатами и цветом
    private val dotPositions = mutableListOf<Dot>()

    // Обработчик кликов
    var onDotClickListener: ((x: Int, y: Int) -> Unit)? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val widthCount = (width / cellSize).toInt()
        val heightCount = (height / cellSize).toInt()

        // Рисуем сетку
        for (i in 0..widthCount) {
            val x = i * cellSize
            canvas.drawLine(x, 0f, x, height.toFloat(), gridPaint)
        }
        for (j in 0..heightCount) {
            val y = j * cellSize
            canvas.drawLine(0f, y, width.toFloat(), y, gridPaint)
        }

        val colorGroups = dotPositions.groupBy { it.color.uppercase() }

        for ((colorKey, group) in colorGroups) {
            val visited = mutableSetOf<Dot>()
            for (dot in group) {
                if (dot !in visited) {
                    val region = mutableSetOf<Dot>()
                    dfs(dot, group, visited, region)

                    val contour = traceBoundary(region)
                    if (contour.size >= 3 && contour.first() == contour.last()) {
                        // Построим Path и зальём
                        val path = Path()
                        path.moveTo(contour[0].x * cellSize, contour[0].y * cellSize)
                        for (i in 1 until contour.size) {
                            val pt = contour[i]
                            path.lineTo(pt.x * cellSize, pt.y * cellSize)
                        }
                        path.close()

                        val fillPaint = Paint().apply {
                            color = when (colorKey) {
                                "RED" -> Color.argb(100, 255, 0, 0)
                                "GREEN" -> Color.argb(100, 0, 255, 0)
                                "BLACK" -> Color.argb(100, 0, 0, 0)
                                else -> Color.argb(100, 128, 128, 128)
                            }
                            style = Paint.Style.FILL
                        }

                        canvas.drawPath(path, fillPaint)
                    }
                }
            }
        }

        // Рисуем точки
        for (dot in dotPositions) {
            dotPaint.color = when (dot.color.uppercase()) {
                "RED" -> Color.RED
                "GREEN" -> Color.GREEN
                "BLACK" -> Color.BLACK
                else -> Color.GRAY
            }

            val x = dot.x * cellSize
            val y = dot.y * cellSize
            canvas.drawCircle(x, y, dotRadius, dotPaint)
        }

    }


    private fun dfs(dot: Dot, group: List<Dot>, visited: MutableSet<Dot>, region: MutableSet<Dot>) {
        val stack = ArrayDeque<Dot>()
        stack.add(dot)

        while (stack.isNotEmpty()) {
            val current = stack.removeLast()
            if (current in visited) continue
            visited.add(current)
            region.add(current)

            for (neighbor in group) {
                if (neighbor !in visited && isAdjacentWithDiagonals(current, neighbor)) {
                    stack.add(neighbor)
                }
            }
        }
    }
    private fun traceBoundary(region: Set<Dot>): List<Dot> {
        val directions = listOf(
            Pair(0, -1),  // вверх
            Pair(1, -1),  // вверх-вправо
            Pair(1, 0),   // вправо
            Pair(1, 1),   // вниз-вправо
            Pair(0, 1),   // вниз
            Pair(-1, 1),  // вниз-влево
            Pair(-1, 0),  // влево
            Pair(-1, -1)  // вверх-влево
        )

        fun nextDirLeft(current: Int): Int = (current + 7) % 8

        val start = region.minWithOrNull(compareBy({ it.y }, { it.x })) ?: return emptyList()
        var current = start
        var dir = 0
        val path = mutableListOf<Dot>()
        val visitedEdges = mutableSetOf<Pair<Dot, Int>>()

        do {
            path.add(current)
            var found = false
            for (i in 0 until 8) {
                dir = (dir + 1) % 8
                val dx = directions[dir].first
                val dy = directions[dir].second
                val neighbor = Dot(current.x + dx, current.y + dy, current.color)
                if (neighbor in region && Pair(current, dir) !in visitedEdges) {
                    visitedEdges.add(Pair(current, dir))
                    current = neighbor
                    dir = (dir + 4) % 8 // Поворачиваем обратно
                    found = true
                    break
                }
            }
            if (!found) break
        } while (current != start && path.size < 1000)

        // Закрыть контур
        if (path.first() != path.last()) {
            path.add(path.first())
        }
        return path
    }







    // Проверка соседства включая диагонали
    private fun isAdjacentWithDiagonals(a: Dot, b: Dot): Boolean {
        val dx = Math.abs(a.x - b.x)
        val dy = Math.abs(a.y - b.y)
        return dx <= 1 && dy <= 1 && !(dx == 0 && dy == 0)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val x = (event.x / cellSize).roundToInt()
            val y = (event.y / cellSize).roundToInt()

            // Не добавляем сюда точку — логика рисования теперь вне view
            onDotClickListener?.invoke(x, y)
            return true
        }
        return super.onTouchEvent(event)
    }

    // Округление
    private fun Float.roundToInt(): Int = (this + 0.5f).toInt()

    // Установка точек извне
    fun setDots(filledPos: List<Dot>) {
        dotPositions.clear()
        dotPositions.addAll(filledPos)
        invalidate()
    }

    // Очистка
    fun clearDots() {
        dotPositions.clear()
        invalidate()
    }
}
