import mu.KotlinLogging
import org.json.JSONObject
import java.lang.RuntimeException

private val logger = KotlinLogging.logger {}
class Strategy {
    fun run() {

        val world = World.init(JSONObject(readLine()).getJSONObject("params"))
        logger.debug { "$world" }

        var goToRandom = false
        var pathToRandom = mutableListOf<Cell>()
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
            val meNorm = Cell((me.pos.first - 15) / 30, (me.pos.second - 15) / 30)

            if (pathToRandom.isEmpty()) {
                goToRandom = !goToRandom

                var randomPointFound = false
                var randomPoint = Cell(0,0)

                if (goToRandom) {
                    while (!randomPointFound) {
                        randomPoint = Cell((1..31).random() * 30 - 15, (1..31).random() * 30 - 15)
                        if (!me.territoryNorm.contains(randomPoint)) {
                            randomPointFound = true
                        }
                    }
                } else {
                    randomPoint = Alg.getBound(me.territory).random()
                }

                logger.debug { "random point: $randomPoint" }
                val barriers = listOf(me.linesNorm.toSet().plus(Cell((lastPos.first - 15) / 30, (lastPos.second - 15) / 30)))
                val (path, cost) = aStarSearch(meNorm, Cell((randomPoint.first - 15) / 30, (randomPoint.second - 15) / 30), SquareGrid(31,31, barriers))
                pathToRandom = path.toMutableList()


                pathToRandom.removeAt(0)
                val move = pathToRandom[0]
                pathToRandom.removeAt(0)

                logger.debug { "1 norm: $meNorm, move: $move toRand: $goToRandom me: ${me.pos}" }
                logger.debug { "1 dir: ${getDirection(meNorm, move)}" }

                lastPos = me.pos
                System.out.printf("{\"command\": \"%s\"}\n", getDirection(meNorm, move))
            } else {
                val move = pathToRandom[0]
                pathToRandom.removeAt(0)

                logger.debug { "2 norm: $meNorm, move: $move toRand: $goToRandom me: ${me.pos}" }
                logger.debug { "2 dir: ${getDirection(meNorm, move)}" }

                lastPos = me.pos
                System.out.printf("{\"command\": \"%s\"}\n", getDirection(meNorm, move))
            }
        }
    }

    private fun getDirection(me: Cell, target: Cell): String {
        return if (target == Cell(me.first + 1, me.second)) {
            "right"
        } else if (target == Cell(me.first - 1, me.second)) {
            "left"
        } else if (target == Cell(me.first, me.second + 1)) {
            "up"
        } else if (target == Cell(me.first, me.second - 1)) {
            "down"
        } else {
            throw RuntimeException("PZDC")
        }
    }

    private fun getMe(players: MutableList<Player>): Player =
        players.find { it.id == 0 } ?: throw RuntimeException("Me not found")

    private fun dummy(world: World, me: Player): String {
        val possibleTurns = mutableListOf<Triple<Int, Int, String>>()

        val up = Pair(me.pos.first, me.pos.second + world.width)
        if (up.second < world.yCells * world.width && !me.lines.contains(up)) {
            possibleTurns.add(Triple(up.first, up.second, "up"))
        }

        val down = Pair(me.pos.first, me.pos.second - world.width)
        if (down.second > 0 && !me.lines.contains(down)) {
            possibleTurns.add(Triple(down.first, down.second, "down"))
        }

        val left = Pair(me.pos.first - world.width, me.pos.second)
        if (left.first > 0 && !me.lines.contains(left)) {
            possibleTurns.add(Triple(left.first, left.second, "left"))
        }

        val right = Pair(me.pos.first + world.width, me.pos.second)
        if (right.first < world.xCells * world.width && !me.lines.contains(right)) {
            possibleTurns.add(Triple(right.first, right.second, "right"))
        }

        return possibleTurns.random().third
    }
}