fun main() {

    val world = World(31, 31, 30, 5)

    val me = Player(1, 0, Cell(1,2), listOf(Cell(855, 885), Cell(855, 915), Cell(885, 915), Cell(915,915), Cell(915, 885), Cell(915, 855), Cell(885, 855)),
        listOf(Cell(855, 855)), "d")

//    val me = Player(1, 0, Cell(1,2), listOf(Cell(105, 135), Cell(105, 165), Cell(135, 165), Cell(165,165), Cell(165, 135), Cell(165, 105), Cell(135, 105)),
//        listOf(Cell(105, 105)), "d")

//    val me = Player(1, 0, Cell(1,2), listOf(Cell(15, 45), Cell(15, 75), Cell(45, 75), Cell(75, 75), Cell(75, 45), Cell(75, 15), Cell(45, 15)),
//        listOf(Cell(15, 15)), "d")

    println(BFS.getFill(me, world))
}