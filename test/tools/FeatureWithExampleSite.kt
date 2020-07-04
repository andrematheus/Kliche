package tools

import tools.client.Client
import tools.factories.Factories

open class FeatureWithExampleSite(sitePath: String) {
    private val site = Factories.siteFromResourcePath(sitePath)
    protected val client = Client.forSite(site)
}