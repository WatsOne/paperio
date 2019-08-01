import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import kotlin.math.max
import kotlin.math.min

class BFS {
    companion object {
        fun getFill(me: Player, world: World): List<Cell> {
            val allMy = me.territoryNorm.plus(me.linesNorm)
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
                for (turn in turns) {
                    val potentialCell = Alg.doTurn(current, turn, 1)
                    if (potentialCell.first in (0 .. (right - left)) && potentialCell.second in (0 .. (top - bottom))) {
                        if (matrix[potentialCell.first][potentialCell.second] == 0) {
                            matrix[potentialCell.first][potentialCell.second] = 2
                            border.add(potentialCell)
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