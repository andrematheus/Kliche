package kliche

import org.tomlj.Toml
import org.tomlj.TomlTable
import java.nio.file.Path

interface SiteConfiguration {
    val sources: List<Source>
}

class TomlFileConfiguration(
    configurationFilePath: Path,
    tomlStringConfiguration: TomlStringConfiguration = TomlStringConfiguration(
        configurationFilePath.toFile().bufferedReader().readText()
    )
) : SiteConfiguration by tomlStringConfiguration

class TomlStringConfiguration(configuration: String) :
    SiteConfiguration {

    private val result = Toml.parse(configuration)

    init {
        // TODO throw specific exception when toml has errors
        assert(!result.hasErrors())
    }

    override val sources: List<Source>
        get() {
            // TODO: throw specific exception instead of !!
            val sourcesTable: TomlTable = result.getTable("sources")!!
            return sourcesTable.keySet().map {
                // TODO: throw specific exception instead of !!
                this.sourceFromConfiguration(sourcesTable.getTable(it)!!)
            }
        }

    private fun sourceFromConfiguration(it: TomlTable): Source {
        val type = it.getString("type")
        // TODO: throw specific exception unknown type is passed
        assert(type == "embedded")
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
        return StaticSource(routesMap.toMap())
    }
}