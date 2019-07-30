import org.json.JSONArray
import org.json.JSONObject

class Player(
    val id: Int,
    val score: Int,
    val pos: Cell,
    val lines: List<Cell>,
    val territory: List<Cell>,
    val direction: String
) {
    companion object Json {
        fun init(json: JSONObject, id: String): Player {
            return Player(
                if (id == "i") 0 else id.toInt(),
                json.getInt("score"),
                json.getJSONArray("position").let { Cell(it.getInt(0), it.getInt(1)) },
                convertPoints(json.getJSONArray("lines")),
                convertPoints(json.getJSONArray("territory")),
                json.get("direction").toString()
            )
        }

        private fun convertPoints(json: JSONArray): List<Cell> =
            json.map { it as JSONArray }.map { Cell(it.getInt(0), it.getInt(1)) }
    }

    val linesNorm = lines.map { Cell((it.first - 15) / 30, (it.second - 15) / 30) }
    val territoryNorm = territory.map { Cell((it.first - 15) / 30, (it.second - 15) / 30) }

    override fun toString(): String {
        return "Player(id=$id, score=$score, pos=$pos, lines=$lines, territory=$territory, direction='$direction')"
    }
}