package features

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import tools.FeatureWithExampleSite

class SiteRendersContentInsideLayout: FeatureWithExampleSite("/site-with-layouts") {
    @Test
    internal fun `should show hello word message inside layout`() {
        client.withSiteRunning {
            val response = get("/")
            assertEquals(200, response.statusCode)
            assertEquals("<Layout>Hello, world!</Layout>", response.text)
        }
    }
}