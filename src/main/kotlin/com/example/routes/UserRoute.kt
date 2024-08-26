package com.example.routes

import com.example.DatabaseManager.database
import com.example.data.model.Tables
import com.example.data.model.UserDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ktorm.entity.add
import org.ktorm.entity.sequenceOf
import java.util.*

fun Route.user() {
    route("/users") {
        post {
            val userVO = call.receive<UserDto>()

            val user = Tables.User {
                id = 0
                username = userVO.username
                password = userVO.password
                email = userVO.email
            }

            database.sequenceOf(Tables.Users).add(user)
            call.respond(HttpStatusCode.Created, "Created")
            return@post
        }
    }
}

private fun generateNewPassword(): String {
    return (Random().nextInt(9000) + 1000).toString()
}