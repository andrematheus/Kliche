package kliche

import io.undertow.Undertow
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange

class Site(
    private val host: String,
    private val port: Int,
    setup: SiteSetup.() -> Unit
) : HttpHandler {

    private val sources = SiteSetup().also(setup).sources
    private val undertow: Undertow = Undertow.builder()
        .addHttpListener(port, host, this)
        .build()

    fun start() {
        undertow.start()
    }

    fun stop() {
        undertow.stop()
    }

    val baseUri: String
        get() = "http://${host}:${port}"

    override fun handleRequest(exchange: HttpServerExchange) {
        val response = this.sources
            .filter { it.handles(exchange.requestPath) }
            .firstOrNull()
            ?.handle(exchange.requestPath)
        if (response != null) {
            exchange.statusCode = 200
            exchange.responseSender.send(response)
        }
    }
}