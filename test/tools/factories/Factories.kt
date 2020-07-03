package tools.factories

import kliche.Site
import kliche.TomlFileConfiguration
import java.nio.file.Path
import kotlin.random.Random
import kotlin.random.nextInt

fun siteFromResourceConfigurationFile(resourceFilePath: String): Site {
    val configurationFilePath = Path.of(
        Site::class.java.getResource(resourceFilePath).toURI()
    )
    return Site(
        "localhost",
        Random.nextInt(5000..9000),
        TomlFileConfiguration(configurationFilePath)
    )
}