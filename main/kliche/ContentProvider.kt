package kliche

import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import java.io.IOException
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

interface SourceFileCompiler {
    fun accepts(file: Path): Boolean
    fun compile(file: Path): String
    fun generatedFileName(file: Path): Path
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

class SourceFileMarkdownCompiler : SourceFileCompiler {
    override fun accepts(file: Path): Boolean {
        return file.toFile().extension == "md"
    }

    override fun generatedFileName(file: Path): Path {
        return Path.of(file.toFile().parent)
            .resolve(file.toFile().nameWithoutExtension + ".html")
    }

    override fun compile(file: Path): String {
        val document =
            Parser.builder()
                .build().parse(file.toFile().bufferedReader().readText())
        val renderer = HtmlRenderer.builder()
            .build()
        return renderer.render(document)
    }

}