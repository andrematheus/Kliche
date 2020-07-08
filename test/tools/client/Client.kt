package tools.client

import io.github.rybalkinsd.kohttp.ext.httpGet
import kliche.Site
import kliche.main
import org.testcontainers.containers.GenericContainer
import java.io.Closeable
import java.io.IOException
import java.net.Socket
import kotlin.concurrent.thread

class Client(private val site: Site? = null) {
    interface ClientOps {
        fun get(path: String): Response
    }

    class SiteClientOps(private val site: Site) : ClientOps {
        override fun get(path: String): Response {
            val response = "${site.baseUri}${path}".httpGet()
            return Response(response.code(), response.body()?.string() ?: "")
        }
    }

    class ContainerClientOps(private val container: GenericContainer<*>) : ClientOps {
        override fun get(path: String): Response {
            val response =
                "http://${container.getHost()}:${container.getFirstMappedPort()}".httpGet()
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
        RunningSite(site!!).use {
            SiteClientOps(site).block()
        }
    }

    fun withMainRunning(path: String, block: ClientOps.() -> Unit) {
        thread(start = true, isDaemon = true) {
            main(arrayOf(path, site?.port.toString()))
        }
        var started = false
        var times = 50
        while (!started && times > 0) {
            try {
                Socket(site!!.host, site.port).use { started = true }
            } catch (e: IOException) {
                times -= 1
                Thread.sleep(500)
            }
        }
        if (times == 0) {
            error("Site did not come up.")
        }
        SiteClientOps(site!!).block()
    }

    fun withContainerRunning(
        container: GenericContainer<*>,
        block: ClientOps.() -> Unit
    ) {
        container.also { it.start() }.use {
            ContainerClientOps(container).block()
        }
    }

    companion object {
        fun forSite(site: Site) = Client(site)
        fun forContainer() = Client()
    }
}