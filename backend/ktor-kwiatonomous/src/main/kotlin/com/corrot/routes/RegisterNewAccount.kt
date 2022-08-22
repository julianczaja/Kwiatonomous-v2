package com.corrot.routes

import com.corrot.calculateHA1
import com.corrot.db.data.dao.UserDao
import com.corrot.db.data.model.RegisterCredentials
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.registerNewAccount(path: String, userDao: UserDao) {
    post(path) {
        try {
            val registerCredentials = call.receive<RegisterCredentials>()

            validateRegisterCredentials(registerCredentials)

            if (userDao.getUser(registerCredentials.login) != null) {
                call.respondText(
                    text = "User named ${registerCredentials.login} already exists",
                    status = HttpStatusCode.BadRequest
                )
                return@post
            }

            userDao.createUser(
                userName = registerCredentials.userName,
                userId = registerCredentials.login,
                ha1 = calculateHA1(registerCredentials.login, registerCredentials.password)
            )

            println("Registered new user: $registerCredentials")
            call.respond(HttpStatusCode.OK)
        } catch (e: Exception) {
            println("/register : $e")
            call.respondText("Incorrect register credentials input", status = HttpStatusCode.BadRequest)
        }
    }
}


@kotlin.jvm.Throws(Exception::class)
fun validateRegisterCredentials(registerCredentials: RegisterCredentials) {
    // TODO: Implement proper validation
    if (registerCredentials.login.length < 5
        || registerCredentials.password.length < 5
        || registerCredentials.userName.length < 5
        || registerCredentials.userName.length > 32
    ) {
        throw Exception("Login or password must be a minimum of 5 characters")
    }
}