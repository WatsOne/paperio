//import mu.KotlinLogging
import org.json.JSONObject
import java.lang.RuntimeException

//private val logger = KotlinLogging.logger {}
class Strategy {
    fun run() {

        val world = World.init(JSONObject(readLine()).getJSONObject("params"))
        //logger.debug { "$world" }
        while (true) {
            val tickInput = JSONObject(readLine())

            if (tickInput.getString("type") == TurnType.END_GAME.value) {
                //logger.debug { "END GAME!" }
                break
            }

            val params = tickInput.getJSONObject("params")
            val tick = params.getInt("tick_num")

            val playersJson = params.getJSONObject("players")
            val players = mutableListOf<Player>()
            playersJson.keys().forEach {
                players.add(Player.init(playersJson.getJSONObject(it), it))
            }

            System.out.printf("{\"command\": \"%s\"}\n", dummy(world, getMe(players)))
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