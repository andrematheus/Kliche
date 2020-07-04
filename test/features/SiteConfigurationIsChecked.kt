package features

import kliche.InvalidSiteConfiguration
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import tools.factories.Factories

class SiteConfigurationIsChecked {
    @Test
    internal fun `invalid source type throws InvalidSiteConfiguration`() {
        val exception = assertThrows<InvalidSiteConfiguration> {
            Factories.siteFromResourcePath(
                "/site-configuration-is-checked/invalid-source-type/"
            )
        }
        assertTrue(exception.message?.contains("Invalid type")!!)
    }

    @Test
    internal fun `invalid compiler type throws InvalidSiteConfiguration`() {
        val exception = assertThrows<InvalidSiteConfiguration> {
            Factories.siteFromResourcePath(
                "/site-configuration-is-checked/invalid-compiler-type/"
            )
        }
        assertTrue(exception.message?.contains("Invalid compiler type")!!)
    }
}