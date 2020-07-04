package kliche

import org.tomlj.Toml
import org.tomlj.TomlTable
import java.nio.file.Path

interface SiteConfiguration {
    val host: String
    val port: Int
    val contentProviders: List<ContentProvider>
}

class TomlFileConfiguration(
    basePath: Path,
    configurationFilePath: Path = basePath.resolve("kliche.toml"),
    tomlStringConfiguration: TomlStringConfiguration = TomlStringConfiguration(
        basePath,
        configurationFilePath.toFile().bufferedReader().readText()
    )
) : SiteConfiguration by tomlStringConfiguration

class TomlStringConfiguration(
    private val basePath: Path,
    configuration: String
) :
    SiteConfiguration {

    private val result = Toml.parse(configuration)
    override val host = result.getString("host")!!
    override val port = result.getLong("port")!!.toInt()

    init {
        // TODO throw specific exception when toml has errors
        assert(!result.hasErrors())
    }

    override val contentProviders: List<ContentProvider>
        get() {
            // TODO: throw specific exception instead of !!
            val sourcesTable: TomlTable = result.getTable("providers")!!
            return sourcesTable.keySet().map {
                // TODO: throw specific exception instead of !!
                this.sourceFromConfiguration(sourcesTable.getTable(it)!!)
            }
        }

    private fun sourceFromConfiguration(it: TomlTable): ContentProvider {
        val type = it.getString("type")
        return when (type) {
            "embedded" -> this.buildEmbeddedProviderFromConfiguration(it)
            "static" -> this.buildStaticProviderFromConfiguration(it)
            "source-files" -> this.buildSourceFilesProviderFromconfiguration(it)
            else -> throw InvalidSiteConfiguration("Invalid type: $type")
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
        val compilers = buildSourceFilesCompilersFromConfiguration(it)
        return SourceFilesContentProvider(basePath.resolve(sourceFilesPath), compilers)
    }

    private fun buildSourceFilesCompilersFromConfiguration(it: TomlTable): List<SourceFileCompiler> {
        val compilersTable = it.getTable("compilers")
        // TODO: throw specific exception instead of !!
        return compilersTable?.keySet()?.map {
            // TODO: throw specific exception instead of !!
            val compilerTable: TomlTable = compilersTable.getTable(it)!!
            val type = compilerTable.getString("type")
            when (type) {
                "markdown" -> SourceFileMarkdownCompiler()
                "jade" -> SourceFileJadeCompiler()
                else -> throw InvalidSiteConfiguration("Invalid compiler type: $type")
            }
        }!!
    }
}

class InvalidSiteConfiguration(reason: String) : Throwable(reason)