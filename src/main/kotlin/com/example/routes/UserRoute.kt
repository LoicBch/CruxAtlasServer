package com.example.routes

import com.example.DatabaseManager.database
import com.example.data.model.Tables
import com.example.data.model.UserProfileDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.SimpleEmail
import org.ktorm.dsl.*
import org.ktorm.entity.add
import org.ktorm.entity.find
import org.ktorm.entity.removeIf
import org.ktorm.entity.sequenceOf
import java.time.LocalDate
import java.util.*

fun Route.user() {
    route("/users") {
        get("/users/{id}/profile") {
            val userId = call.parameters["id"]!!.toInt()
            val user = database.sequenceOf(Tables.Users).find { it.id eq userId }!!
            val userProfile = UserProfileDto(
                user.username,
                user.email,
                user.creationDate.toString()
            )
            call.respond(HttpStatusCode.BadRequest, userProfile)
            return@get
        }

        post("/{id}/log") {
            val routeId = call.request.queryParameters["route_id"]!!.secured().toInt()
            val logText = call.request.queryParameters["log_text"]!!.secured()
            val userId = call.parameters["id"]!!.secured().toInt()

            val log = Tables.RouteLog {
                this.userId = userId
                this.routeId = routeId
                this.text = logText
                this.date = LocalDate.now()
            }

            database.sequenceOf(Tables.RouteLogs).add(log)
            return@post
        }

        get("/{id}/favorite") {
            val userId = call.parameters["id"]?.toInt()!!

            val crags = database.from(Tables.UserFavCrags).select(Tables.UserFavCrags.spotId)
                .where { Tables.UserFavCrags.userId eq userId }.map { Tables.UserFavCrags.createEntity(it) }
                .map { table -> database.sequenceOf(Tables.Crags).find { it.id eq table.cragId } }

            call.respond(
                HttpStatusCode.OK, crags
            )
        }

        post("/{id}/favorite") {

            val userId = call.parameters["id"]!!.secured().toInt()

            val favRelation = Tables.UserFavCrag {
                this.cragId = call.request.queryParameters["spot_id"]!!.secured().toInt()
                this.userId = userId
            }
            database.sequenceOf(Tables.UserFavCrags).add(favRelation)

            val spotsIds = database.from(Tables.UserFavCrags).select(Tables.UserFavCrags.spotId)
                .where { Tables.UserFavCrags.userId eq userId }.map { Tables.UserFavCrags.createEntity(it).cragId }

            call.respond(HttpStatusCode.OK, spotsIds)
            return@post
        }

        delete("/{id}/favorite") {
            val userId = call.parameters["id"]?.secured()?.toInt()!!
            val spotId = call.request.queryParameters["spot_id"]?.secured()?.toInt()!!
            database.sequenceOf(Tables.UserFavCrags).removeIf { (it.userId eq userId) and (it.spotId eq spotId) }

            val spotsIds = database.from(Tables.UserFavCrags).select(Tables.UserFavCrags.spotId)
                .where { Tables.UserFavCrags.userId eq userId }.map { Tables.UserFavCrags.createEntity(it).cragId }

            call.respond(HttpStatusCode.OK, spotsIds)
        }

        post("/resetPassword") {
            val userMail = call.request.queryParameters["mail"]?.secured()!!
            val newPass = generateNewPassword()

            try {
                val user = database.sequenceOf(Tables.Users).find { it.email eq userMail }!!
                user.password = newPass
                user.flushChanges()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "User not found")
                return@post
            }

            val email = SimpleEmail()
            email.hostName = "smtp.gmail.com"
            email.setSmtpPort(587)
            email.setAuthenticator(DefaultAuthenticator("cruxatlas3d@gmail.com", "3DClimbapp1."))
            email.isSSLOnConnect = true
            email.setFrom("cruxatlas3d@gmail.com")
            email.subject = "Crux Atlas - Your new password"
            email.setMsg("Hi your new password is $newPass")
            email.addTo(userMail)
            email.send()

            call.respond(HttpStatusCode.OK)
            return@post
        }

        put("/{id}/password") {
            val userId = call.parameters["id"]?.secured()?.toInt()!!
            val user = database.sequenceOf(Tables.Users).find { it.id eq userId }!!
            user.password = call.request.queryParameters["password"]?.secured()!!
            user.flushChanges()
            call.respond(HttpStatusCode.OK)
        }

        put("/{id}/email") {
            val userId = call.parameters["id"]?.secured()?.toInt()!!
            val user = database.sequenceOf(Tables.Users).find { it.id eq userId }!!
            user.email = call.request.queryParameters["email"]!!.secured()
            user.flushChanges()
            call.respond(HttpStatusCode.OK)
            return@put
        }

    }
}

private fun generateNewPassword(): String {
    return (Random().nextInt(9000) + 1000).toString()
}