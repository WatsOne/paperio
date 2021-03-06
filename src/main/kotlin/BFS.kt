import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import kotlin.math.max
import kotlin.math.min

class BFS {
    companion object {
        fun getFill(territory: List<Cell>, lines: List<Cell>, world: World): List<Cell> {
            val linesNorm = lines.map { Cell((it.first - 15) / 30, (it.second - 15) / 30) }
            val territoryNorm = territory.map { Cell((it.first - 15) / 30, (it.second - 15) / 30) }
            val allMy = territoryNorm.plus(linesNorm)
            val shift = 2

            val top = min(world.yCells - 1, (allMy.maxBy { it.second }?.second ?: 0) + shift)
            val bottom = max(0, (allMy.minBy { it.second }?.second ?: 0) - shift)
            val right = min(world.xCells - 1, (allMy.maxBy { it.first }?.first ?: 0) + shift)
            val left = max(0, (allMy.minBy { it.first }?.first ?: 0) - shift)

            val border = LinkedBlockingQueue<Cell>()
            val xRange = (left..right)
            val yRange = (bottom..top)

            yRange.forEach {
                addIfNotMy(border, allMy, Cell(left, it), Cell(0, it - bottom))
                addIfNotMy(border, allMy, Cell(right, it), Cell(right - left, it - bottom))
            }
            xRange.forEach {
                addIfNotMy(border, allMy, Cell(it, top), Cell(it - left, top - bottom))
                addIfNotMy(border, allMy, Cell(it, bottom), Cell(it - left, 0))
            }

            val matrix = Array(xRange.count()) { IntArray(yRange.count()) }
            xRange.forEach { i ->
                yRange.forEach {
                    matrix[i - left][it - bottom] = 0
                }
            }

            allMy.forEach {
                matrix[it.first - left][it.second - bottom] = 1
            }

            val turns = Turn.values()
            while (border.isNotEmpty()) {
                val current = border.poll()
                turns.map { Alg.doTurn(current, it, 1) }.plus(current).forEach {
                    if (it.first in (0 .. (right - left)) && it.second in (0 .. (top - bottom))) {
                        if (matrix[it.first][it.second] == 0) {
                            matrix[it.first][it.second] = 2
                            border.add(it)
                        }
                    }
                }
            }

//            for (i in (0 until xRange.count())) {
//                for (j in (0 until yRange.count())) {
//                    print(matrix[i][j])
//                }
//                println()
//            }

            val result = mutableListOf<Cell>()

            for (i in (0 until xRange.count())) {
                for (j in (0 until yRange.count())) {
                    if (matrix[i][j] == 0) {
                        result.add(Cell((i + left) * 30 + 15, (j + bottom) * 30 + 15))
                    }
                }
            }

            return result
        }

        private fun addIfNotMy(queue: Queue<Cell>, my: List<Cell>, cell: Cell, toPut: Cell) {
            if (!my.contains(cell)) {
                queue.add(toPut)
            }
        }
    }
}