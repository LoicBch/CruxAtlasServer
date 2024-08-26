package com.example.routes

import PHPass
import com.example.DatabaseManager
import com.example.data.model.*
import com.example.security.token.TokenClaim
import com.example.security.token.TokenConfig
import com.example.security.token.TokenService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.Route
import org.ktorm.dsl.*
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import org.owasp.encoder.Encode
import java.text.SimpleDateFormat
import java.util.*


fun Route.auth(tokenService: TokenService, tokenConfig: TokenConfig) {
    post("/login") {
        val authRequest = call.receive<AuthRequest>()
        val checker = PHPass(8)

        val user = DatabaseManager.database.sequenceOf(Tables.Users).find { it.username eq authRequest.username }
        var logMatches = false

        user?.let {
            logMatches = checker.checkPassword(authRequest.password, user.password)
        }

        if (user == null || !logMatches) {
            call.respond(
                HttpStatusCode.Unauthorized, ErrorResponse("username or password incorrect")
            )
            return@post
        }
        val token = tokenService.generate(tokenConfig, TokenClaim("user_id", user.id.toString()))
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val formattedDate = dateFormat.format(currentDate)
        user.lastConnection = formattedDate
        user.flushChanges()

        val userDto = UserDto(
            user.id!!,
            user.username,
            user.password,
            user.email
        )

        call.respond(
            HttpStatusCode.OK, AuthResponse(token, userDto)
        )
    }

    authenticate {
        get("/authenticate") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("user_id", String::class)
            val user = DatabaseManager.database.sequenceOf(Tables.Users).find { it.id eq userId!!.toInt() }!!
            val userDto = UserDto(
                user.id!!,
                user.username,
                user.password,
                user.email
            )

            call.respond(HttpStatusCode.OK, user)
        }
    }
}

