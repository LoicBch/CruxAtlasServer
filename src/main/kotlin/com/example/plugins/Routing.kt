package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.DatabaseManager
import com.example.routes.*
import com.example.security.token.TokenConfig
import com.example.security.token.TokenService
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.http.content.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import java.util.*

fun Application.configureRouting(tokenService: TokenService, tokenConfig: TokenConfig) {
    routing {
        get {
            call.respond(
                HttpStatusCode.OK,
                "hello world 0.0.13"
            )
        }

        auth(tokenService, tokenConfig)
        user()
        route()
        feed()
        models()
        crags()
        areas()
        utils()
    }
}
