package kliche.shell

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Path

fun Path.readText() = this.toFile().bufferedReader().readText()

fun <T : Any> T.logger(): Logger = LoggerFactory.getLogger(javaClass)