package com.corrot.plugins

import io.ktor.server.application.*
import io.ktor.server.logging.*
import io.ktor.server.plugins.callloging.*
import org.slf4j.event.Level

fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
        format { call ->
            "${call.request.toLogString()}\t|\tStatus: ${call.response.status()}"
        }
    }
}
