typealias Cell = Pair<Int, Int>
enum class Turn(val str: String) {
    LEFT("left"), RIGHT("right"), TOP("up"), BOTTOM("down")
}
enum class State {
    INIT,
    GO_TO_NEAREST_BOUND,
    MAIN_MOVE,
    RUN
}

data class Bbox(val left: Int, val right: Int, val top: Int, val bottom: Int)