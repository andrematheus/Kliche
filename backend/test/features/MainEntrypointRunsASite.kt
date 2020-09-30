package features

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import tools.client.Client
import tools.factories.Factories

class MainEntrypointRunsASite {
    @Test
    internal fun `should run embedded content site when invoked from its directory`() {
        val (site, resourcePath) = Factories.siteAndTempPathFromResourcePath("/embedded-source-site")
        val client = Client(site)
        client.withMainRunning(resourcePath.toFile().absolutePath) {
            val (statusCode, text) = get("/")
            assertEquals(200, statusCode)
            assertEquals("Hello, world!", text)
        }
    }
}