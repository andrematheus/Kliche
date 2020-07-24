package features

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import tools.FeatureWithExampleSite

class SiteRendersContentInsideLayout: FeatureWithExampleSite("/site-with-layouts") {
    @Test
    internal fun `should show hello word message inside layout`() {
        client.withSiteRunning {
            val response = get("/index.html")
            assertEquals(200, response.statusCode)
            assertEquals("<Layout>Hello, world!</Layout>", response.text)
        }
    }

    @Test
    internal fun `should compile layout while rendering content inside of it`() {
        client.withSiteRunning {
            val response = get("/with-jade.html")
            assertEquals(200, response.statusCode)
            assertEquals("<h1>Hello, world!</h1>", response.text)
        }
    }

    @Test
    internal fun `should put layout only on content that wants it`() {
        client.withSiteRunning {
            val response = get("/css/test.css")
            assertEquals(200, response.statusCode)
            assertEquals("/* css file */", response.text)
        }
    }

    @Test
    internal fun `should put layout on index`() {
        client.withSiteRunning {
            val response = get("/with-index")
            assertEquals(200, response.statusCode)
            assertEquals("<h1><p>Hello world</p></h1>", response.text)
        }
    }
}