package kliche

import de.neuland.jade4j.Jade4J
import kliche.shell.readText
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.lesscss.LessCompiler
import java.nio.file.Path

interface SourceFileCompiler {
    fun accepts(file: Path): Boolean
    fun compile(file: Path): String
    fun generatedFileName(file: Path): Path
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
                .build().parse(file.readText())
        val renderer = HtmlRenderer.builder()
            .build()
        return renderer.render(document)
    }

}

class SourceFileJadeCompiler : SourceFileCompiler {
    override fun accepts(file: Path): Boolean {
        return file.toFile().extension == "jade"
    }

    override fun generatedFileName(file: Path): Path {
        return Path.of(file.toFile().parent)
            .resolve(file.toFile().nameWithoutExtension + ".html")
    }

    override fun compile(file: Path): String {
        return Jade4J.render(
            file.toFile().absolutePath,
            mapOf()
        )
    }
}

class SourceFileLessCompiler : SourceFileCompiler {
    override fun accepts(file: Path): Boolean {
        return file.toFile().extension == "less"
    }

    override fun compile(file: Path): String {
        return LessCompiler().compile(file.toFile())
    }

    override fun generatedFileName(file: Path): Path {
        return Path.of(file.toFile().parent)
            .resolve(file.toFile().nameWithoutExtension + ".css")
    }
}