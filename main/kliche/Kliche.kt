package kliche

import java.nio.file.Path
import kotlin.concurrent.thread

fun main(args: Array<String>) {
    val path = args[0]
    val site = Site(Path.of(path))
    Runtime.getRuntime().addShutdownHook(thread(start = false) {
        site.stop()
    })
    site.start()
}