package kliche

import java.nio.file.Files
import java.nio.file.Path

interface ContentProvider {
    fun has(requestPath: String): Boolean
    fun get(requestPath: String): String
}

class EmbeddedContentProvider(private val routes: Map<String, String>) : ContentProvider {
    override fun has(requestPath: String) = requestPath in routes

    override fun get(requestPath: String): String {
        return routes.getValue(requestPath)
    }
}

class StaticContentProvider(private val basePath: Path) : ContentProvider {
    override fun has(requestPath: String) =
        Files.exists(basePath.resolve(requestPath.removePrefix("/")))

    override fun get(requestPath: String) =
        basePath.resolve(requestPath.removePrefix("/")).toFile().readText()
}