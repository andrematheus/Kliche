package kliche

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.Mustache
import io.undertow.server.HttpHandler
import io.undertow.server.handlers.resource.CachingResourceManager
import io.undertow.server.handlers.resource.FileResourceManager
import io.undertow.server.handlers.resource.ResourceHandler
import io.undertow.util.StatusCodes
import org.apache.tika.Tika
import java.io.File
import java.io.StringReader
import java.io.StringWriter
import java.nio.ByteBuffer
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes

sealed class BytesConvertible {
    abstract fun bytes(): ByteArray

    data class OfString(val string: String) : BytesConvertible() {
        override fun bytes() = string.toByteArray()
    }
}

fun String.bytesConvertible() = BytesConvertible.OfString(this)

interface ContentProvider {
    fun toHandler(next: HttpHandler? = null): HttpHandler
}

interface FullContentProvider : ContentProvider {
    fun get(requestPath: String): BytesConvertible?

    override fun toHandler(next: HttpHandler?): HttpHandler {
        return HttpHandler { exchange ->
            get(exchange.requestPath)?.also {
                exchange.statusCode = StatusCodes.OK
                exchange.responseSender.send(ByteBuffer.wrap(it.bytes()))
            } ?: next?.handleRequest(exchange)
        }
    }
}

class LayoutProvider(
    layoutFilePath: Path,
    private val contentProvider: FullContentProvider,
    compilers: List<SourceFileCompiler>?
) : FullContentProvider {

    private val mustache: Mustache

    init {
        val firstCompiler =
            compilers?.asSequence()?.firstOrNull { it.accepts(layoutFilePath) }
        val reader = if (firstCompiler != null) {
            StringReader(firstCompiler.compile(layoutFilePath))
        } else {
            layoutFilePath.toFile().bufferedReader()
        }
        mustache = DefaultMustacheFactory().compile(
            reader, layoutFilePath.fileName.toString()
        )
    }

    override fun get(requestPath: String): BytesConvertible? {
        val content = contentProvider.get(requestPath)
        return if ((requestPath.endsWith(".html") || requestPath.lastIndexOf(".") == -1)
            && content is BytesConvertible.OfString
        ) {
            content.let {
                StringWriter().also { sb ->
                    mustache.execute(sb, mapOf("content" to it.string))
                }.toString().bytesConvertible()
            }
        } else {
            content
        }
    }
}

class EmbeddedContentProvider(private val routes: Map<String, String>) :
    FullContentProvider {

    override fun get(requestPath: String): BytesConvertible? {
        return routes[requestPath]?.bytesConvertible()
    }
}

class StaticContentProvider(private val basePath: Path) : ContentProvider {
    override fun toHandler(next: HttpHandler?) =
        ResourceHandler(FileResourceManager(basePath.toFile()), next)
            .setWelcomeFiles("index.html")
}

private fun isIndexfile(path: Path) =
    path.fileName.toString().startsWith("index.") && probablyIsText(path)

val tika = lazy { Tika() }

private fun probablyIsText(path: Path) =
    tika.value.detect(path).startsWith("text")

class SourceFilesContentProvider(
    sourceFilesPath: Path,
    val compilers: List<SourceFileCompiler>
) : FullContentProvider {

    val routes = mutableMapOf<String, FileWithCompiler>()

    data class FileWithCompiler(val filePath: Path, val compiler: SourceFileCompiler)

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
                    routes[generatedFilename] = FileWithCompiler(file, it)
                    if (isIndexfile(file)) {
                        val parentFilename =
                            sourceFilesPath.relativize(file.parent).toString()
                        routes[parentFilename] = FileWithCompiler(file, it)
                    }
                }
            return FileVisitResult.CONTINUE
        }
    }

    init {
        Files.walkFileTree(sourceFilesPath, this.visitor)
    }

    override fun get(requestPath: String): BytesConvertible? {
        return routes[requestPath.removePrefix("/").removeSuffix("/")]?.let {
            it.compiler.compile(it.filePath).bytesConvertible()
        }
    }
}