package helloworld

import kliche.Site
import kliche.TomlStringConfiguration
import org.junit.Test
import tools.client.Client
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.test.assertEquals

internal class HelloWorldSiteTest {
    private val sitePort = Random.nextInt(5000..9000)
    private val siteConfiguration = """
        title = "Hello world kliche site"

        [sources]

        [sources.static-source]
        type = "static"

        [[sources.static-source.routes]]
        path = "/"
        content = "Hello, world!"
    """.trimIndent()

    private val site = Site("localhost", sitePort, TomlStringConfiguration(siteConfiguration))
    private val client = Client.forSite(site)

    @Test
    fun `should show hello world message at root`() {
        client.withSiteRunning {
            val response = get("/")
            assertEquals("Hello, world!", response.text)
        }
    }
}