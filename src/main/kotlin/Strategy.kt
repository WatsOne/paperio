import java.util.*

class Strategy {
    public fun run() {
        val scanner = Scanner(System.`in`)
        val start = scanner.next()
        println("Start $start")
        while (true) {
            val input = scanner.next()
            System.out.printf("{\"command\": \"%s\"}\n", "up")
        }
    }
}