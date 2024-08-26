package com.example.plugins

import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*

fun Application.configureHTTP() {
    install(DefaultHeaders) {
        header("X-Engine", "Ktor")
        header("Content-Security-Policy", "default-src 'self'; script-src 'self' https://easyurbex.com/; style-src 'self' https://easyurbex.com/; img-src 'self' https://easyurbex.com/;")
        header("X-Content-Type-Options", "nosniff")
        header("X-Frame-Options", "DENY")
        header("X-XSS-Protection", "1; mode=block")
        header("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload")
        header("X-Permitted-Cross-Domain-Policies", "none")
        header("Referrer-Policy", "no-referrer")
        header("Feature-Policy", "vibrate 'none'; usb 'none'; payment 'none';")
        header("Permissions-Policy", "geolocation=(), midi=(), sync-xhr=(), microphone=(), camera=(), magnetometer=(), gyroscope=(), fullscreen=(self), payment=(self)")
    }
}
