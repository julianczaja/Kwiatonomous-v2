package com.corrot.routes

import com.corrot.db.data.dao.UserDao
import com.corrot.db.data.model.toUserDto
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getUser(path: String, userDao: UserDao) {
    get(path) {
        val userId = call.principal<UserIdPrincipal>()?.name
        if (userId == null) {
            call.respond(HttpStatusCode.BadRequest, "Not authenticated")
            return@get
        }

        val user = userDao.getUser(userId)
        if (user != null) {
            call.respond(HttpStatusCode.OK, user.toUserDto())
        } else {
            call.respond(HttpStatusCode.NotFound, "Can't find user of id: $userId")
        }
    }
}
