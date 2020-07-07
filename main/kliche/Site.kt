package kliche

import io.undertow.Undertow
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.util.StatusCodes
import java.nio.file.Path

class Site(sitePath: Path) : HttpHandler {
    private val configuration: SiteConfiguration = TomlFileConfiguration(sitePath)

    val host: String = configuration.host
    val port: Int = configuration.port

    private val providers = configuration.contentProviders
    private val undertow: Undertow = Undertow.builder()
        .addHttpListener(port, host, this)
        .build()

    fun start() {
        print("Starting kliche on ${host} and ${port}")
        undertow.start()
    }

    fun stop() {
        undertow.stop()
    }

    val baseUri: String
        get() = "http://${host}:${port}"

    override fun handleRequest(exchange: HttpServerExchange) {
        val response = this.providers.asSequence()
            .map { it.get(exchange.requestPath) }
            .firstOrNull()
        if (response != null) {
            exchange.statusCode = StatusCodes.OK
            exchange.responseSender.send(response)
        } else {
            exchange.statusCode = StatusCodes.NOT_FOUND
            exchange.responseSender.send(StatusCodes.NOT_FOUND_STRING)
        }
    }
}