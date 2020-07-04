package features

import org.junit.jupiter.api.Test
import tools.FeatureWithExampleSite
import kotlin.test.assertEquals

internal class SiteShowsEmbeddedContent: FeatureWithExampleSite("/embedded-source-site") {
    @Test
    internal fun `should show hello world message at root`() {
        client.withSiteRunning {
            val response = get("/")
            assertEquals(200, response.statusCode)
            assertEquals("Hello, world!", response.text)
        }
    }

    @Test
    internal fun `should return not found for any other path`() {
        client.withSiteRunning {
            val response = get("/other-path")
            assertEquals(404, response.statusCode)
        }
    }
}