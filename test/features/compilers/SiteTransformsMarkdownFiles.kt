package features.compilers

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import tools.FeatureWithExampleSite

class SiteTransformsMarkdownFiles : FeatureWithExampleSite("/site-with-markdown-files") {
    @Test
    internal fun `should compile markdown file before returning`() {
        client.withSiteRunning {
            val response = get("/test.html")
            assertEquals(200, response.statusCode)
            assertEquals(
                //language=HTML
                """
                <h1>Title</h1>
                <p><strong>Hello, world!</strong></p>
                
                """.trimIndent(),
                response.text
            )
        }
    }

    @Test
    internal fun `should compile markdown index`() {
        client.withSiteRunning {
            val response = get("/")
            assertEquals(200, response.statusCode)
            assertEquals(
                //language=HTML
                """
                <h1>Hello from Markdown</h1>
                
                """.trimIndent(),
                response.text
            )
        }
    }

    @Test
    internal fun `should compile markdown index when url ends in slash`() {
        client.withSiteRunning {
            val response = get("/subdir/")
            assertEquals(200, response.statusCode)
            assertEquals(
                //language=HTML
                """
                <h1>Hello from Markdown</h1>
                
                """.trimIndent(),
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