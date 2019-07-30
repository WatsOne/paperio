import org.json.JSONObject

class World(val xCells: Int, val yCells: Int, val width: Int, val speed: Int) {
    companion object Json {
        fun init(json: JSONObject): World {
            return World(
                json.getInt("x_cells_count"),
                json.getInt("y_cells_count"),
                json.getInt("width"),
                json.getInt("speed")
            )
        }
    }

    override fun toString(): String {
        return "World(xCells=$xCells, yCells=$yCells, width=$width, speed=$speed)"
    }
}