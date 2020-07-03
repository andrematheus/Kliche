package kliche

import io.undertow.Undertow
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange

class Site(
    configuration: SiteConfiguration
) : HttpHandler {
    val host: String  = configuration.host
    val port: Int = configuration.port

    private val sources = configuration.sources
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
            .firstOrNull { it.handles(exchange.requestPath) }
            ?.handle(exchange.requestPath)
        if (response != null) {
            exchange.statusCode = 200
            exchange.responseSender.send(response)
        }
    }
}