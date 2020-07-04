package kliche

import io.undertow.Undertow
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.util.StatusCodes
import java.nio.file.Path

class Site(
    sitePath: Path,
    configuration: SiteConfiguration = TomlFileConfiguration(
        sitePath
    )
) : HttpHandler {

    private val host: String = configuration.host
    private val port: Int = configuration.port

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
            exchange.statusCode = StatusCodes.OK
            exchange.responseSender.send(response)
        } else {
            exchange.statusCode = StatusCodes.NOT_FOUND
            exchange.responseSender.send(StatusCodes.NOT_FOUND_STRING)
        }
    }
}