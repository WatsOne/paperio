class Alg {
    companion object {
        public fun getBound(territory: List<Cell>): List<Cell> {
            val result = mutableListOf<Cell>()
            val turns = Turn.values()
            territory.forEach {
                for (turn in turns) {
                    if (!territory.contains(doTurn(it, turn, 30))) {
                        result.add(it)
                        return@forEach
                    }
                }
            }

            return result
        }

        public fun doTurn(cell: Cell, turn: Turn, step: Int): Cell {
            return when (turn) {
                Turn.LEFT -> Cell(cell.first - step, cell.second)
                Turn.RIGHT -> Cell(cell.first + step, cell.second)
                Turn.TOP -> Cell(cell.first, cell.second + step)
                else -> Cell(cell.first, cell.second - step)
            }
        }
    }
}