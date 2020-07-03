package helloworld

import kliche.Site
import kliche.TomlFileConfiguration
import org.junit.jupiter.api.Test
import tools.client.Client
import java.nio.file.Path
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.test.assertEquals

internal class HelloWorldSiteTest {
    private val sitePort = Random.nextInt(5000..9000)
    private val configurationFilePath = Path.of(javaClass
        .getResource("/hello-world-site/kliche.toml").toURI())

    private val site =
        Site("localhost", sitePort, TomlFileConfiguration(configurationFilePath))
    private val client = Client.forSite(site)

    @Test
    fun `should show hello world message at root`() {
        client.withSiteRunning {
            val response = get("/")
            assertEquals("Hello, world!", response.text)
        }
    }
}