fun main() {

    val world = World(31, 31, 30, 5)

//    val me = Player(1, 0, Cell(1,2), listOf(Cell(855, 885), Cell(855, 915), Cell(885, 915), Cell(915,915), Cell(915, 885), Cell(915, 855), Cell(885, 855)),
//        listOf(Cell(855, 855)), "d")

//    val me = Player(1, 0, Cell(1,2), listOf(Cell(105, 135), Cell(105, 165), Cell(135, 165), Cell(165,165), Cell(165, 135), Cell(165, 105), Cell(135, 105)),
//        listOf(Cell(105, 105)), "d")

//    val me = Player(1, 0, Cell(1,2), listOf(Cell(15, 45), Cell(15, 75), Cell(45, 75), Cell(75, 75), Cell(75, 45), Cell(75, 15), Cell(45, 15)),
//        listOf(Cell(15, 15)), "d")

    val me = Player(1, 0, Cell(1,2), listOf(Cell(15, 45), Cell(15, 75)),
    listOf(Cell(45, 15), Cell(45, 45), Cell(45, 75), Cell(45, 105), Cell(45, 135), Cell(15, 135)), "d")
//
    println(BFS.getFill(me.territory, me.lines, world))


//    val me = Player(1, 0, Cell(1,2), listOf(),
//        listOf(Cell(15, 15), Cell(15, 45), Cell(15, 75), Cell(45, 75), Cell(75, 75), Cell(75, 45), Cell(75, 15), Cell(45, 15), Cell(45, 45)), "d")
//
//    println(Alg.getBound(me.territory))


//    val barriers = listOf(setOf(Pair(1,1), Pair(1,2), Pair(2,1), Pair(2,2), Pair(3,1), Pair(3,2), Pair(4,2)))
//    val barriers = listOf(setOf<Cell>())

//    val (path, cost) = aStarSearch(Cell(9,14), Cell(30,29), SquareGrid(31,31, barriers))
//
//    println("Cost: $cost  Path: $path")
//    test(0)

//    val strategy = Strategy()
//    val bbox = strategy.bbox(listOf(15 to 45, 45 to 45, 45 to 15), 150)
//    println(strategy.getPossibleDirection((15 to 45), "down", listOf(15 to 45, 45 to 45, 45 to 15), listOf(), bbox))
}

private fun test(a: Int) {
    println(a)
    if (a == 5) {
        return
    } else {
        test(a.inc())
    }
}