import mu.KotlinLogging
import org.json.JSONObject

private val logger = KotlinLogging.logger {}
class Strategy {
    fun run() {

        val world = World.init(JSONObject(readLine()).getJSONObject("params"))

        while (true) {
            val tickInput = JSONObject(readLine())

            if (tickInput.getString("type") == TurnType.END_GAME.value) {
                logger.debug { "END GAME!" }
                break
            }

            val params = tickInput.getJSONObject("params")
            val tick = params.getInt("tick_num")

            logger.debug { "tick: $tick" }
            val playersJson = params.getJSONObject("players")
            val players = mutableListOf<Player>()
            playersJson.keys().forEach {
                players.add(Player.init(playersJson.getJSONObject(it), it))
            }

            logger.debug { "me: ${players[0]}" }
            System.out.printf("{\"command\": \"%s\"}\n", "up")
        }
    }
}