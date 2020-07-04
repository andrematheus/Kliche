package features

import org.junit.jupiter.api.Test
import tools.client.Client
import tools.factories.Factories
import kotlin.test.assertEquals

class SiteShowsStaticContentFromFileSystem {
    private val site = Factories.siteFromResourcePath("/static-content-site/")

    private val client = Client.forSite(site)

    @Test
    internal fun `should show content from file`() {
        client.withSiteRunning {
            val response = get("/hello.txt")
            assertEquals(200, response.statusCode)
            assertEquals("Hello, world!", response.text)
        }
    }
}