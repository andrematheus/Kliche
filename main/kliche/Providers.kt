package kliche

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.Mustache
import org.apache.tika.Tika
import java.io.File
import java.io.StringReader
import java.io.StringWriter
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes

sealed class BytesConvertible {
    abstract fun bytes(): ByteArray

    class OfByteArray(private val byteArray: ByteArray) : BytesConvertible() {
        override fun bytes() = byteArray
    }

    data class OfString(val string: String) : BytesConvertible() {
        override fun bytes() = string.toByteArray()
    }
}

fun ByteArray.bytesConvertible() = BytesConvertible.OfByteArray(this)
fun String.bytesConvertible() = BytesConvertible.OfString(this)

interface ContentProvider {
    fun get(requestPath: String): BytesConvertible?
}

class LayoutProvider(
    layoutFilePath: Path,
    private val contentProvider: ContentProvider,
    compilers: List<SourceFileCompiler>?
) : ContentProvider {

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
            && content is BytesConvertible.OfString) {
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

class EmbeddedContentProvider(private val routes: Map<String, String>) : ContentProvider {
    override fun get(requestPath: String): BytesConvertible? {
        return routes[requestPath]?.bytesConvertible()
    }
}

class StaticContentProvider(private val basePath: Path) : ContentProvider {
    private fun has(requestPath: String) =
        Files.exists(basePath.resolve(requestPath.removePrefix("/")))

    override fun get(requestPath: String) =
        when {
            this.has(requestPath) ->
                basePath.resolve(requestPath.removePrefix("/"))
                    .toFile().readFileToBytesConvertible()
            else -> null
        }
}

private fun File.readFileToBytesConvertible() = when {
    this.isFile -> {
        if (probablyIsText(this.toPath())) {
            this.readText().bytesConvertible()
        } else {
            this.readBytes().bytesConvertible()
        }
    }
    this.isDirectory -> {
        val indexes = this.list { _, name ->
            isIndexfile(this.resolve(name).toPath())
        }
        if (indexes?.isNotEmpty() == true) {
            this.resolve(indexes[0]).readText().bytesConvertible()
        } else {
            null
        }
    }
    else -> null
}

private fun isIndexfile(path: Path) =
    path.fileName.toString().startsWith("index.") && probablyIsText(path)

val tika = lazy { Tika() }

private fun probablyIsText(path: Path) =
    tika.value.detect(path).startsWith("text")

class SourceFilesContentProvider(
    sourceFilesPath: Path,
    val compilers: List<SourceFileCompiler>
) : ContentProvider {

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