package kliche.shell

import java.nio.file.Path

fun Path.readText() = this.toFile().bufferedReader().readText()