package helloworld

import kliche.Site
import org.junit.Test
import tools.client.Client
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.test.assertEquals

internal class HelloWorldSiteTest {
    private val sitePort = Random.nextInt(5000..9000)
    private val site = Site("localhost", sitePort) {
        staticSource {
            route("/", "Hello, world!")
        }
    }
    private val client = Client.forSite(site)

    @Test
    fun `should show hello world message at root`() {
        client.withSiteRunning {
            val response = get("/")
            assertEquals("Hello, world!", response.text)
        }
    }
}