package kliche

import java.nio.file.Path
import kotlin.concurrent.thread

fun main(args: Array<String>) {
    val path = args[0]
    var port: Int? = null
    if (args.size > 1) {
        port = args[1].toInt()
    }
    val site = Site(Path.of(path), port)
    Runtime.getRuntime().addShutdownHook(thread(start = false) {
        site.stop()
    })
    site.start()
}