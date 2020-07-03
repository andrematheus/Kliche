package tools.client

import io.github.rybalkinsd.kohttp.ext.httpGet
import kliche.Site
import java.io.Closeable

class Client(private val site: Site) {
    class ClientOps(private val site: Site) {
        fun get(path: String): Response {
            val response = "${site.baseUri}${path}".httpGet()
            return Response(response.code(), response.body()?.string() ?: "")
        }
    }

    internal class RunningSite(private val site: Site) : Closeable {
        init {
            site.start()
        }

        override fun close() {
            site.stop()
        }
    }

    fun withSiteRunning(block: ClientOps.() -> Unit) {
        RunningSite(site).use {
            ClientOps(site).block()
        }
    }

    companion object {
        fun forSite(site: Site) = Client(site)
    }
}