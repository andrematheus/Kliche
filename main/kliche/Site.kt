package kliche

import io.undertow.Undertow
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.util.StatusCodes
import kliche.shell.logger
import java.nio.ByteBuffer
import java.nio.file.Path

class Site(sitePath: Path, overridePort: Int? = null) : HttpHandler {
    private val configuration: SiteConfiguration = TomlFileConfiguration(sitePath)

    val host: String = configuration.host
    val port: Int = overridePort ?: configuration.port

    private val providers = configuration.contentProviders
    private val undertow: Undertow = Undertow.builder()
        .addHttpListener(port, host, this)
        .build()

    fun start() {
        println("Starting kliche on ${host} and ${port}")
        undertow.start()
    }

    fun stop() {
        undertow.stop()
    }

    val baseUri: String
        get() = "http://${host}:${port}"

    override fun handleRequest(exchange: HttpServerExchange) {
        try {
            val response = this.providers.asSequence()
                .map { it.get(exchange.requestPath) }
                .firstOrNull { it != null }
            if (response != null) {
                exchange.statusCode = StatusCodes.OK
                exchange.responseSender.send(ByteBuffer.wrap(response.bytes()))
            } else {
                exchange.statusCode = StatusCodes.NOT_FOUND
                exchange.responseSender.send(StatusCodes.NOT_FOUND_STRING)
            }
        } catch (e: Exception) {
            this.logger().error(e.message, e)
        }
    }
}