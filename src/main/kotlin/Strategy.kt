//import mu.KotlinLogging
import org.json.JSONObject
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.RuntimeException

//private val logger = KotlinLogging.logger {}
class Strategy {
    fun run() {

        val world = World.init(JSONObject(readLine()).getJSONObject("params"))
//        logger.debug { "$world" }

        var pathToBound = mutableListOf<Cell>()
        var pathToMove = mutableListOf<Cell>()
        var state = State.INIT
        var lastPos = Cell(0,0)

        while (true) {
            val tickInput = JSONObject(readLine())

            if (tickInput.getString("type") == TurnType.END_GAME.value) {
                break
            }

            val params = tickInput.getJSONObject("params")
            val tick = params.getInt("tick_num")

            val playersJson = params.getJSONObject("players")
            val players = mutableListOf<Player>()
            playersJson.keys().forEach {
                players.add(Player.init(playersJson.getJSONObject(it), it))
            }

            val me = getMe(players)

            if (state == State.INIT) {
                pathToBound = getNearestPath(me)
                state = State.GO_TO_NEAREST_BOUND
            }

            if (state == State.GO_TO_NEAREST_BOUND) {
                if (pathToBound.isEmpty()) {
                    state = State.MAIN_MOVE

//                    val start = System.currentTimeMillis()
                    val possiblePath = mutableListOf<Pair<List<Cell>, Int>>()
                    doStep(0, me.direction, me.pos, listOf(), me, world, possiblePath)
                    pathToMove = possiblePath.maxBy { it.second }?.first?.toMutableList() ?: throw RuntimeException("PZDC MAX")
//                    logger.debug { "CC: ${System.currentTimeMillis() - start} ms" }

                } else {
                    val nextCell = pathToBound[0]
                    pathToBound.removeAt(0)
                    System.out.printf("{\"command\": \"%s\"}\n", getDirection(me.posNorm, nextCell))
                }
            }

            if (state == State.MAIN_MOVE) {
                val nextCell = pathToMove[0]
                pathToMove.removeAt(0)

                if (pathToMove.isEmpty()) {
                    state = State.INIT
                }

                System.out.printf("{\"command\": \"%s\"}\n", getDirectionMove(me.pos, nextCell))
            }

        }
    }

    private fun getDirection(me: Cell, target: Cell): String {
        return when (target) {
            Cell(me.first + 1, me.second) -> "right"
            Cell(me.first - 1, me.second) -> "left"
            Cell(me.first, me.second + 1) -> "up"
            Cell(me.first, me.second - 1) -> "down"
            else -> throw RuntimeException("PZDC")
        }
    }

    private fun getDirectionMove(me: Cell, target: Cell): String {
        return when (target) {
            Cell(me.first + 30, me.second) -> "right"
            Cell(me.first - 30, me.second) -> "left"
            Cell(me.first, me.second + 30) -> "up"
            Cell(me.first, me.second - 30) -> "down"
            else -> throw RuntimeException("PZDC")
        }
    }

    private fun getMe(players: MutableList<Player>): Player =
        players.find { it.id == 0 } ?: throw RuntimeException("Me not found")

    private fun getNearestPath(me: Player): MutableList<Cell> {
        val barriers = listOf(setOf(getPrevCell(me)))
        val world = SquareGrid(31,31, barriers)
        val bounds = Alg.getBound(me.territory).minus(me.pos)
            .map { Cell((it.first - 15) / 30, (it.second - 15) / 30) }
            .map { try {
                aStarSearch(me.posNorm, it, world)
            } catch (ex: IllegalArgumentException) {
                Pair(listOf<Cell>(), 999999)
            } }
        val path = bounds.minBy { it.second }?.first?.toMutableList() ?: throw RuntimeException("Cannot find nearest path to bound")
        path.removeAt(0)
        return path
    }

    private fun getPrevCell(me: Player): Cell {
        return when (me.direction) {
            "right" -> Cell(me.posNorm.first - 1, me.posNorm.second)
            "left" -> Cell(me.posNorm.first + 1, me.posNorm.second)
            "up" -> Cell(me.posNorm.first, me.posNorm.second - 1)
            else -> Cell(me.posNorm.first, me.posNorm.second + 1)
        }
    }

    private fun getPrevCellMove(cell: Cell, direction: String): Cell {
        return when (direction) {
            "right" -> Cell(cell.first - 30, cell.second)
            "left" -> Cell(cell.first + 30, cell.second)
            "up" -> Cell(cell.first, cell.second - 30)
            else -> Cell(cell.first, cell.second + 30)
        }
    }

//    private fun getPath(me: Player): List<Cell> {
//        val path = ArrayDeque<Cell>()
//        val visitedCells = mutableListOf<Cell>()
//
//
//
//        var depth = 0
//        while (!path.isEmpty()) {
//            val step = path.pollFirst()
//            depth++
//            if (me.territory.contains(step)) {
//                //stop
//            } else {
//
//            }
//        }
//    }
//
//
    private fun doStep(depth: Int, direction: String, pos: Cell, path: List<Cell>, me: Player, world: World, res: MutableList<Pair<List<Cell>, Int>>) {
        val last = nearTerr(pos, me.territory)
        if (last != null) {
            val p = path.plus(last)
//            logger.debug { "FOUND! d: $depth, path: $p, cos: ${}" }
            res.add(Pair(p, p.size + BFS.getFill(me.territory, p, world).size))
        }
        if (depth < 10) {
            getPossibleDirection(pos, direction, me.territory).forEach {
                doStep(depth.inc(), it.second, it.first, path.plus(it.first), me, world, res)
            }
        }
    }

    private fun nearTerr(pos: Cell, terr: List<Cell>): Cell? {
        for (turn in Turn.values()) {
            val turnCell = Alg.doTurn(pos, turn, 30)
            if (terr.contains(turnCell)) {
                return turnCell
            }
        }

        return null
    }

    private fun getPossibleDirection(pos: Cell, direction: String, terr: List<Cell>): List<Pair<Cell, String>> {
        val possible = mutableListOf<Pair<Cell, String>>()
        for (turn in Turn.values()) {
            val possibleCell = Alg.doTurn(pos, turn, 30)
            if (possibleCell != getPrevCellMove(pos, direction) && possibleCell.first in (0..930) && possibleCell.second in (0..930)) {
                if (!terr.contains(possibleCell)) {
                    possible.add(Pair(possibleCell, turn.str))
                }
            }
        }

        return possible
    }
}