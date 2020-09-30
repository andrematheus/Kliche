package features.compilers

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import tools.FeatureWithExampleSite

class SiteTransformsLessFiles : FeatureWithExampleSite("/site-with-less-files") {
    @Test
    fun `should compile less file before returning it`() {
        val expectedCss = """
                    #header {
                      width: 10px;
                      height: 20px;
                    }
                    
                    
                """.trimIndent()
        client.withSiteRunning {
            val response = get("/test.css")
            assertEquals(200, response.statusCode)
            assertEquals(
                //language=CSS
                expectedCss,
                response.text?.replace("\r", "")
            )
        }
    }
}