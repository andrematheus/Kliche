package features

import org.junit.jupiter.api.Test
import tools.client.Client
import tools.factories.siteFromResourceConfigurationFile
import kotlin.test.assertEquals

internal class EmbeddedContent {
    private val site =
        siteFromResourceConfigurationFile("/embedded-source-site/kliche.toml")

    private val client = Client.forSite(site)

    @Test
    fun `should show hello world message at root`() {
        client.withSiteRunning {
            val response = get("/")
            assertEquals("Hello, world!", response.text)
        }
    }
}