package com.corrot.plugins

import io.ktor.application.*
import io.ktor.features.*
import org.slf4j.event.Level

fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
        format { call ->
            "${call.request.toLogString()}\t|\tStatus: ${call.response.status()}"
        }
    }
}
