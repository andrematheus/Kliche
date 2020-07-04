package tools.factories

import kliche.Site
import org.apache.commons.io.FileUtils
import java.nio.file.CopyOption
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

class Factories {
    companion object {
        fun siteFromResourcePath(resourcePath: String): Site {
            val tempPath = Files.createTempDirectory("kliche-test")
            val resourceSitePath = Path.of(Site::class.java.getResource(resourcePath).toURI())
            FileUtils.copyDirectory(resourceSitePath.toFile(), tempPath.toFile())
            return Site(tempPath)
        }
    }
}