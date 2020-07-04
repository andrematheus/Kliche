package kliche

import java.nio.file.Files
import java.nio.file.Path

interface Source {
    fun handles(requestPath: String): Boolean
    fun handle(requestPath: String): String
}

class EmbeddedSource(private val routes: Map<String, String>) : Source {
    override fun handles(requestPath: String) = requestPath in routes

    override fun handle(requestPath: String): String {
        return routes.getValue(requestPath)
    }
}

class StaticSource(private val basePath: Path) : Source {
    override fun handles(requestPath: String) =
        Files.exists(basePath.resolve(requestPath.removePrefix("/")))

    override fun handle(requestPath: String) =
        basePath.resolve(requestPath.removePrefix("/")).toFile().readText()
}