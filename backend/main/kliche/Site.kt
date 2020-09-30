package kliche

import io.undertow.Undertow
import io.undertow.server.HttpHandler
import io.undertow.server.HttpServerExchange
import io.undertow.server.handlers.ResponseCodeHandler
import io.undertow.util.StatusCodes
import kliche.shell.logger
import java.nio.ByteBuffer
import java.nio.file.Path

class Site(sitePath: Path, overridePort: Int? = null) {
    private val configuration: SiteConfiguration = TomlFileConfiguration(sitePath)

    val host: String = configuration.host
    val port: Int = overridePort ?: configuration.port

    private val handler: HttpHandler = configuration.contentProviders
        .foldRight(ResponseCodeHandler(404) as HttpHandler, ContentProvider::toHandler)

    private val undertow: Undertow = Undertow.builder()
        .addHttpListener(port, host)
        .setHandler(handler)
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
}