package tools.factories

import kliche.Site
import kliche.TomlFileConfiguration
import java.nio.file.Path
import kotlin.random.Random
import kotlin.random.nextInt

class Factories {
    companion object {
        fun siteFromResourcePath(resourcePath: String): Site {
            return Site(
                Path.of(Site::class.java.getResource(resourcePath).toURI())
            )
        }
    }
}