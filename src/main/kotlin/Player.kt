import org.json.JSONArray
import org.json.JSONObject

class Player(
    val id: Int,
    val score: Int,
    val pos: Pair<Int, Int>,
    val lines: List<Pair<Int, Int>>,
    val territory: List<Pair<Int, Int>>,
    val direction: String
) {
    companion object Json {
        fun init(json: JSONObject, id: String): Player {
            return Player(
                if (id == "i") 0 else id.toInt(),
                json.getInt("score"),
                json.getJSONArray("position").let { Pair(it.getInt(0), it.getInt(1)) },
                convertPoints(json.getJSONArray("lines")),
                convertPoints(json.getJSONArray("territory")),
                json.get("direction").toString()
            )
        }

        private fun convertPoints(json: JSONArray): List<Pair<Int, Int>> =
            json.map { it as JSONArray }.map { Pair(it.getInt(0), it.getInt(1)) }
    }
}