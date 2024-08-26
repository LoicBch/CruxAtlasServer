package com.example

import io.ktor.server.application.*
import com.example.plugins.*
import com.example.security.token.JwtTokenService
import com.example.security.token.TokenConfig
import io.ktor.http.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.text.get

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {

    val tokenService = JwtTokenService()
    val tokenConfig = TokenConfig(
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        expireIn = 365L * 1000L * 60L * 60L * 24L,
        secret = System.getenv("JWT_SECRET")
    )

    configureSecurity(tokenConfig)
    configureRouting(tokenService, tokenConfig)
    configureSerialization()
    configureMonitoring()
    configureHTTP()

}

