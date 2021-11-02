package com.corrot.plugins

import com.corrot.di.MainModule
import io.ktor.application.*
import org.koin.ktor.ext.Koin

fun Application.configureKoin() {
    install(Koin) {
        modules(MainModule)
    }
}
