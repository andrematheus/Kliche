package features

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import tools.FeatureWithExampleSite

class SiteTransformsJadeFiles : FeatureWithExampleSite("/site-with-jade-files") {
    @Test
    internal fun `should compile jade file before returning`() {
        client.withSiteRunning {
            val response = get("/test.html")
            assertEquals(200, response.statusCode)
            assertEquals(
                //language=HTML
                "<h1>Title</h1><p><strong>Hello, world!</strong></p>",
                response.text
            )
        }
    }

    @Test
    internal fun `should return 404 when no markdown file would have requested url`() {
        client.withSiteRunning {
            val response = get("/non-existing.html")
            assertEquals(404, response.statusCode)
        }
    }
}