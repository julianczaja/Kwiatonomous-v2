package com.corrot.plugins

import com.corrot.di.MainModule
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin

fun Application.configureKoin() {
    install(Koin) {
        modules(MainModule)
    }
}
