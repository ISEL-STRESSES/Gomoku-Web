package gomoku.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GomokuWebApplication

fun main(args: Array<String>) {
    runApplication<GomokuWebApplication>(*args)
}
