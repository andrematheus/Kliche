package features

import org.junit.jupiter.api.Test
import tools.FeatureWithExampleSite
import kotlin.test.assertEquals

class SiteShowsStaticContentFromFileSystem: FeatureWithExampleSite("/static-content-site/") {
    @Test
    internal fun `should show content from file`() {
        client.withSiteRunning {
            val response = get("/hello.txt")
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