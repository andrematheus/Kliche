package kliche

import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes

interface ContentProvider {
    fun get(requestPath: String): String?
}

class EmbeddedContentProvider(private val routes: Map<String, String>) : ContentProvider {
    override fun get(requestPath: String): String? {
        return routes[requestPath]
    }
}

class StaticContentProvider(private val basePath: Path) : ContentProvider {
    private fun has(requestPath: String) =
        Files.exists(basePath.resolve(requestPath.removePrefix("/")))

    override fun get(requestPath: String) =
        when {
            this.has(requestPath) ->
                basePath.resolve(requestPath.removePrefix("/")).toFile().readText()
            else -> null
        }
}

class SourceFilesContentProvider(
    sourceFilesPath: Path,
    val compilers: List<SourceFileCompiler>
) : ContentProvider {

    val routes = mutableMapOf<String, Pair<Path, SourceFileCompiler>>()

    private val visitor = object : SimpleFileVisitor<Path>() {
        override fun visitFile(
            file: Path,
            attrs: BasicFileAttributes
        ): FileVisitResult {
            compilers.asSequence()
                .firstOrNull { it.accepts(file) }
                ?.also {
                    val generatedFilename =
                        sourceFilesPath.relativize(it.generatedFileName(file)).toString()
                    routes[generatedFilename] = Pair(file, it)
                }
            return FileVisitResult.CONTINUE
        }
    }

    init {
        Files.walkFileTree(sourceFilesPath, this.visitor)
    }

    override fun get(requestPath: String): String? {
        return routes[requestPath.removePrefix("/")]?.let {
            it.second.compile(it.first)
        }
    }
}