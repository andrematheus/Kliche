package features

import kliche.InvalidSiteConfiguration
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
        assert(exception.message?.contains("Invalid type")!!)
    }
}