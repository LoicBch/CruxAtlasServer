package com.example

import com.example.plugins.configureRouting
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.callloging.*
import org.slf4j.event.*
import io.ktor.server.request.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.http.content.*
import io.ktor.server.auth.*
import io.ktor.util.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlin.test.*
import io.ktor.server.testing.*
import com.example.plugins.*
import io.ktor.client.utils.EmptyContent.status

//class ApplicationTest {
//    @Test
//    fun testRoot() = testApplication {
//        application {
////            configureRouting()
//        }
////        client.get("/").apply {
////            assertEquals(HttpStatusCode.OK, status)
////            assertEquals("Hello World!", bodyAsText())
////        }
//    }
//}