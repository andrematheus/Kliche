package kliche

import kliche.shell.readText
import org.tomlj.Toml
import org.tomlj.TomlInvalidTypeException
import org.tomlj.TomlTable
import java.nio.file.Path

interface SiteConfiguration {
    val host: String
    val port: Int
    val contentProviders: List<ContentProvider>
}

class TomlFileConfiguration(
    tomlStringConfiguration: TomlStringConfiguration
) : SiteConfiguration by tomlStringConfiguration {

    constructor(basePath: Path) : this(
        TomlStringConfiguration(
            basePath,
            basePath.resolve("kliche.toml").readText()
        )
    )
}

class TomlStringConfiguration(
    private val basePath: Path,
    configuration: String
) :
    SiteConfiguration {

    private val result = Toml.parse(configuration)
    override val host = result.getString("host") ?: "0.0.0.0"
    override val port = result.getLong("port")?.toInt() ?: 8080

    init {
        if (result.hasErrors()) {
            throw InvalidSiteConfiguration(
                "Invalid TOML file: ${result.errors().map { it.toString() }
                    .joinToString()}"
            )
        }
    }

    override val contentProviders: List<ContentProvider>
        get() {
            try {
                val providersTable: TomlTable = result.getTable("providers")
                    ?: throw InvalidSiteConfiguration("No providers found")
                return providersTable.keySet().map {
                    // TODO: throw specific exception instead of !!
                    this.sourceFromConfiguration(providersTable.getTable(it)!!)
                }
            } catch (e: TomlInvalidTypeException) {
                throw InvalidSiteConfiguration("Toml error: ${e.message ?: ""}", e)
            }
        }

    private fun sourceFromConfiguration(it: TomlTable): ContentProvider {
        val contentProvider = when (val type = it.getString("type")) {
            "embedded" -> this.buildEmbeddedProviderFromConfiguration(it)
            "static" -> this.buildStaticProviderFromConfiguration(it)
            "source-files" -> this.buildSourceFilesProviderFromconfiguration(it)
            else -> throw InvalidSiteConfiguration("Invalid type: $type")
        }
        return if (it.contains("layout")) {
            val compilers = buildSourceFilesCompilersFromConfiguration(it)
            LayoutProvider(
                basePath.resolve(it.getString("layout")!!),
                contentProvider,
                compilers
            )
        } else {
            contentProvider
        }
    }

    private fun buildEmbeddedProviderFromConfiguration(it: TomlTable): EmbeddedContentProvider {
        // TODO: throw specific exception instead of !!
        val routes = it.getArray("routes")!!
        val routesMap = mutableMapOf<String, String>()
        routes.toList().map {
            // TODO: throw specific exception instead of !!
            it as TomlTable
            // TODO: throw specific exception instead of !!
            val path = it.getString("path")!!
            // TODO: throw specific exception instead of !!
            val content = it.getString("content")!!
            routesMap[path] = content
        }
        return EmbeddedContentProvider(routesMap.toMap())
    }

    private fun buildStaticProviderFromConfiguration(it: TomlTable): StaticContentProvider {
        // TODO: throw specific exception instead of !!
        val path = it.getString("path")!!
        return StaticContentProvider(basePath.resolve(path))
    }

    private fun buildSourceFilesProviderFromconfiguration(it: TomlTable): ContentProvider {
        // TODO: throw specific exception instead of !!
        val sourceFilesPath = it.getString("path")!!
        val compilers = buildSourceFilesCompilersFromConfiguration(it)!!
        return SourceFilesContentProvider(basePath.resolve(sourceFilesPath), compilers)
    }

    private fun buildSourceFilesCompilersFromConfiguration(it: TomlTable): List<SourceFileCompiler>? {
        val compilersTable = it.getTable("compilers")
        // TODO: throw specific exception instead of !!
        return compilersTable?.keySet()?.map {
            // TODO: throw specific exception instead of !!
            val compilerTable: TomlTable = compilersTable.getTable(it)!!
            when (val type = compilerTable.getString("type")) {
                "markdown" -> SourceFileMarkdownCompiler()
                "jade" -> SourceFileJadeCompiler()
                "less" -> SourceFileLessCompiler()
                else -> throw InvalidSiteConfiguration("Invalid compiler type: $type")
            }
        }
    }
}

class InvalidSiteConfiguration(reason: String, cause: Throwable? = null) :
    Throwable(reason, cause)