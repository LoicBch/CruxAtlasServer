package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.security.token.TokenConfig
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

fun Application.configureSecurity(tokenConfig: TokenConfig) {

    authentication {
        jwt {
            val env  = this@configureSecurity.environment
            realm = env.config.property("jwt.realm").getString()

            verifier(
                JWT
                    .require(Algorithm.HMAC256(tokenConfig.secret))
                    .withAudience(tokenConfig.audience)
                    .withIssuer(tokenConfig.issuer)
                    .build()
            )

            validate {
                if (it.payload.audience.contains(tokenConfig.audience)) {
                    JWTPrincipal(it.payload)
                } else null
            }

            challenge { defaultScheme, realm ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }

}
