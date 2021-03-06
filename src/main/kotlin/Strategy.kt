//import mu.KotlinLogging
import org.json.JSONObject
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.RuntimeException
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

//private val logger = KotlinLogging.logger {}
class Strategy {
    fun run() {

        val world = World.init(JSONObject(readLine()).getJSONObject("params"))
//        logger.debug { "$world" }

        var pathToBound = mutableListOf<Cell>()
        var pathToMove = mutableListOf<Cell>()
        var pathToRun = mutableListOf<Cell>()
        var prevMinDistance = -1
        var state = State.INIT

//        val test = mutableListOf("up", "up", "up", "up", "up", "up", "up", "up", "up", "up", "up", "up", "up", "up", "up", "up",
//            "right", "right", "right", "right", "right", "right", "right", "right", "right", "right", "right", "right", "right", "right", "right", "right",
//            "down", "down", "down", "down", "down", "down",
//            "right", "right", "right", "right",
//            "down", "down", "down", "down", "down", "down")

        while (true) {
            val tickInput = JSONObject(readLine())

//            if (test.isNotEmpty()) {
//                val doTest = test[0]
//                test.removeAt(0)
//                System.out.printf("{\"command\": \"%s\"}\n", doTest)
//                continue
//            }

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
//            val enemiesDirection = players.filter { it.id != 0 }.map { it.pos to it.direction }

            if (state == State.INIT) {
                pathToBound = getNearestPath(me)
                state = State.GO_TO_NEAREST_BOUND
                prevMinDistance = -1
            }

            if (state == State.GO_TO_NEAREST_BOUND) {
                if (pathToBound.isEmpty()) {
                    state = State.MAIN_MOVE

//                    val start = System.currentTimeMillis()
                    val possiblePath = mutableListOf<Pair<List<Cell>, Int>>()
                    val bbox = bbox(me.territory.plus(me.lines), 150)
                    doStep(0, me.direction, me.pos, listOf(), me, world, possiblePath, bbox)
                    pathToMove = possiblePath.maxBy { it.second }?.first?.toMutableList() ?: throw RuntimeException("PZDC MAX")
//                    logger.debug { ">> $tick: ${System.currentTimeMillis() - start} ms" }

                } else {
                    val nextCell = pathToBound[0]
                    pathToBound.removeAt(0)
                    System.out.printf("{\"command\": \"%s\"}\n", getDirection(me.posNorm, nextCell))
                }
            }

            if (state == State.MAIN_MOVE) {
                var doRun = false
//                val tickToBoom = if (me.lines.isEmpty()) null else tickToBoom(enemiesDirection, me, pathToMove.size)
//                if (tickToBoom != null) {
//                    pathToRun = getPathToRun(me, tickToBoom)
//                    if (pathToRun.isNotEmpty()) {
//                        state = State.RUN
//                        doRun = true
//                    }
//                }
                val nearestPlayer = getNearestPlayer(me, players)
                if (nearestPlayer != null && nearestPlayer.second < 390) {
                    val nearestDistance = getDistanceNearest2Lines(me, nearestPlayer.first)
                    if (nearestDistance < prevMinDistance) {
                        pathToRun = getPathToRun(me, 50)
                        if (pathToRun.isNotEmpty()) {
                            state = State.RUN
                            doRun = true
                        }
                    } else {
                        prevMinDistance = nearestDistance
                    }
                }


                if (!doRun) {
                    val nextCell = pathToMove[0]
                    pathToMove.removeAt(0)

                    if (pathToMove.isEmpty()) {
                        state = State.INIT
                    }

                    System.out.printf("{\"command\": \"%s\"}\n", getDirectionMove(me.pos, nextCell))
                }
            }

            if (state == State.RUN) {
                val nextCell = pathToRun[0]
                pathToRun.removeAt(0)

                if (pathToRun.isEmpty()) {
                    state = State.INIT
                }

                System.out.printf("{\"command\": \"%s\"}\n", getDirection(me.posNorm, nextCell))
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
    private fun doStep(depth: Int, direction: String, pos: Cell, path: List<Cell>, me: Player,
                       world: World, res: MutableList<Pair<List<Cell>, Int>>, bbox: Bbox) {
        val last = nearTerr(pos, me.territory, direction)
        if (last != null && path.isNotEmpty()) {
            val p = path.plus(last)
//            logger.debug { "FOUND! d: $depth, path: $p, cost: ${p.size + BFS.getFill(me.territory, p, world).size}" }
            res.add(Pair(p, p.size + BFS.getFill(me.territory, p, world).size))
        }
        if (depth < 11) {
            getPossibleDirection(pos, direction, me.territory, path, bbox).forEach {
                doStep(depth.inc(), it.second, it.first, path.plus(it.first), me, world, res, bbox)
            }
        }
    }

    private fun nearTerr(pos: Cell, terr: List<Cell>, direction: String): Cell? {
        for (turn in Turn.values()) {
            val turnCell = Alg.doTurn(pos, turn, 30)
            if (terr.contains(turnCell) && turnCell != getPrevCellMove(pos, direction)) {
                return turnCell
            }
        }

        return null
    }

    public fun getPossibleDirection(pos: Cell, direction: String, terr: List<Cell>, currentPath: List<Cell>, bbox: Bbox): List<Pair<Cell, String>> {
        val possible = mutableListOf<Pair<Cell, String>>()
        for (turn in Turn.values()) {
            val possibleCell = Alg.doTurn(pos, turn, 30)
            if (possibleCell != getPrevCellMove(pos, direction) && possibleCell.first in (0..930) && possibleCell.second in (0..930)) {
                if (!terr.contains(possibleCell) && !currentPath.contains(possibleCell)) {
                    if (possibleCell.first in (bbox.left..bbox.right) && possibleCell.second in (bbox.bottom..bbox.top)) {
                        possible.add(Pair(possibleCell, turn.str))
                    }
                }
            }
        }

        return possible
    }

    public fun bbox(allMy: List<Cell>, shift: Int): Bbox {
        val top = min(930, (allMy.maxBy { it.second }?.second ?: 0) + shift)
        val bottom = max(0, (allMy.minBy { it.second }?.second ?: 0) - shift)
        val right = min(930, (allMy.maxBy { it.first }?.first ?: 0) + shift)
        val left = max(0, (allMy.minBy { it.first }?.first ?: 0) - shift)
        return Bbox(left, right, top, bottom)
    }

    private fun tickToBoom(enemiesDirection: List<Pair<Cell, String>>, me: Player, toEnd: Int): Int? {
        val ticks = mutableListOf<Int>()
        enemiesDirection.forEach { e ->
            (1..toEnd).forEach {
                val possibleCell = when (e.second) {
                    "left" -> Cell(e.first.first - it*30, e.first.second)
                    "right" -> Cell(e.first.first + it*30, e.first.second)
                    "up" -> Cell(e.first.first, e.first.second + it*30)
                    else -> Cell(e.first.first, e.first.second - it*30)
                }

                if (me.lines.contains(possibleCell)) {
                    ticks.add(it)
                }
            }
        }

        return ticks.min()
    }

    private fun getPathToRun(me: Player, tickToBoom: Int): MutableList<Cell> {
        val barriers = listOf(me.linesNorm.plus(getPrevCell(me)).toSet())
        val world = SquareGrid(31,31, barriers)
        val bounds = getRunCandidates(me).map { Cell((it.first - 15) / 30, (it.second - 15) / 30) }

        for (cell in bounds) {
            try {
                val (path, cost) = aStarSearch(me.posNorm, cell, world)
                if (cost <= tickToBoom) {
                    val normPath = path.toMutableList()
                    normPath.removeAt(0)
                    return normPath
                }
            } catch (ex: IllegalArgumentException) {
                //meh
            }
        }
        return mutableListOf()
    }

    private fun getRunCandidates(me: Player): List<Cell> {
        return Alg.getBound(me.territory).map { it to manhattan(me.pos, it) }.sortedBy { it.second }.map { it.first }.take(10)
    }

    private fun manhattan(x: Cell, y: Cell): Int {
        return abs(x.first - y.first) + abs(x.second - y.second)
    }

    private fun getNearestPlayer(me: Player, players: List<Player>): Pair<Player, Int>? {
        return players.filter { it.id != 0 }.map { it to manhattan(me.pos, it.pos) }.minBy { it.second }
    }

    private fun getDistanceNearest2Lines(me: Player, enemy: Player): Int {
        return me.lines.map { manhattan(it, enemy.pos) }.min() ?: -1
    }
}