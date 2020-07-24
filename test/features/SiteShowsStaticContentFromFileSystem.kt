package features

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import tools.FeatureWithExampleSite

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
    internal fun `should return binary content as well`() {
        val pngFileBytes = SiteShowsStaticContentFromFileSystem::class.java
            .getResource("/static-content-site/static-files/pngfile.png").readBytes()
        client.withSiteRunning {
            val response = get("/pngfile.png")
            assertEquals(200, response.statusCode)
            assertTrue(pngFileBytes.contentEquals(response.bytes!!))
        }
    }

    @Test
    internal fun `should return index file for folders`() {
        client.withSiteRunning {
            val response = get("/dir")
            assertEquals(200, response.statusCode)
            assertEquals("Hello from index", response.text)
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