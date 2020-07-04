package features

import org.junit.jupiter.api.Test
import tools.FeatureWithExampleSite
import kotlin.test.assertEquals

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
}