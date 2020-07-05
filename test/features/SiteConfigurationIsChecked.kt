package features

import kliche.InvalidSiteConfiguration
import kliche.Site
import kliche.TomlStringConfiguration
import kliche.shell.readText
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import tools.factories.Factories
import java.nio.file.Path

class SiteConfigurationIsChecked {
    @Test
    internal fun `invalid toml file throws InvalidSiteConfiguration`() {
        val exception = assertThrows<InvalidSiteConfiguration> {
            Factories.siteFromResourcePath(
                "/site-configuration-is-checked/invalid-toml/"
            )
        }
        assertTrue(exception.message?.contains("Invalid TOML file")!!)
    }

    @Test
    internal fun `if no host is found in file then localhost is used`() {
        val resourceSitePath = Path.of(
            Site::class.java.getResource("/site-configuration-is-checked/no-host").toURI()
        )
        val configuration =
            TomlStringConfiguration(resourceSitePath, resourceSitePath.resolve("kliche.toml").readText())
        assertEquals("localhost", configuration.host)
    }

    @Test
    internal fun `if no port is found in file then 8080 is used`() {
        val resourceSitePath = Path.of(
            Site::class.java.getResource("/site-configuration-is-checked/no-port").toURI()
        )
        val configuration =
            TomlStringConfiguration(resourceSitePath, resourceSitePath.resolve("kliche.toml").readText())
        assertEquals(8080, configuration.port)
    }

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
    internal fun `no providers in file throws InvalidSiteConfiguration`() {
        val exception = assertThrows<InvalidSiteConfiguration> {
            Factories.siteFromResourcePath(
                "/site-configuration-is-checked/no-providers/"
            )
        }
        assertTrue(exception.message?.contains("No providers found")!!)
    }

    @Test
    internal fun `provider child is not table throws InvalidSiteConfiguration `() {
        val exception = assertThrows<InvalidSiteConfiguration> {
            Factories.siteFromResourcePath(
                "/site-configuration-is-checked/providers-table-has-child-not-table/"
            )
        }
        assertTrue(exception.message?.contains("Toml error:")!!)
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