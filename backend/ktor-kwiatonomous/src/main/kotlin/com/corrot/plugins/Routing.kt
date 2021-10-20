package com.corrot.plugins

import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("Welcome to Kwiatonomous project!")
        }

        route("/dupa") {

            get("/1") {
                call.respondText("Dupa 1")
            }

            post("/add") {
                call.respond(mapOf("dupa" to call.receiveText()))
            }
        }
    }
}