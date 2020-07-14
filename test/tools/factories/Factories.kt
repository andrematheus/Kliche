package tools.factories

import kliche.Site
import org.apache.commons.io.FileUtils
import java.io.IOException
import java.net.DatagramSocket
import java.net.ServerSocket
import java.nio.file.CopyOption
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.random.Random
import kotlin.random.nextInt

class Factories {
    companion object {
        fun siteFromResourcePath(resourcePath: String): Site {
            val (site, _) = this.siteAndTempPathFromResourcePath(resourcePath)
            return site
        }

        fun siteAndTempPathFromResourcePath(resourcePath: String): Pair<Site, Path> {
            val tempPath = Files.createTempDirectory("kliche-test")
            val resourceSitePath =
                Path.of(Site::class.java.getResource(resourcePath).toURI())
            FileUtils.copyDirectory(resourceSitePath.toFile(), tempPath.toFile())
            return Pair(Site(tempPath, randomAvailablePort()), tempPath)
        }

    }
}

fun randomAvailablePort(): Int {
    val port = Random.nextInt(5000..9000)
    if (isPortAvailable(port)) {
        return port
    } else {
        return randomAvailablePort()
    }
}

fun isPortAvailable(port: Int): Boolean {
    try {
        ServerSocket(port).use { it.reuseAddress = true }
        DatagramSocket(port).use { it.reuseAddress = true}
        return true
    } catch (e: IOException) {
        return false
    }
}