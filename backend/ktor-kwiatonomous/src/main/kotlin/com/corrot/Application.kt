package com.corrot

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.corrot.plugins.*

fun main() {
    embeddedServer(Netty, port = 20188, host = "192.168.1.188") {
        configureRouting()
        configureSerialization()
        configureMonitoring()
    }.start(wait = true)
}
