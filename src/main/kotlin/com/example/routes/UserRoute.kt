package com.example.routes

import com.example.DatabaseManager.database
import com.example.data.model.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.Route
import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.SimpleEmail
import org.ktorm.dsl.*
import org.ktorm.entity.*
import java.io.File
import java.time.LocalDate
import java.util.*

fun Route.user() {
    route("/users") {

        patch("/{id}") {
            val userVO = call.receive<UserDto>()
            val user = database.sequenceOf(Tables.Users).find { it.username eq userVO.username }
            if (user != null) {
                database.update(Tables.Users) {
                    set(it.age, userVO.age)
                    set(it.city, userVO.city)
                    set(it.country, userVO.country)
                    if (userVO.climbingSince == "") {
                        set(it.climbingSince, null)
                    } else {
                        set(it.climbingSince, LocalDate.parse(userVO.climbingSince))
                    }
                    set(it.email, userVO.email)
                    set(it.height, userVO.height)
                    set(it.weight, userVO.weight)
                    set(it.gender, userVO.gender)

                    if (userVO.imageUrl == null) {
                        set(it.imageUrl, "")
                    } else {
                        set(it.imageUrl, userVO.imageUrl)
                    }

                    where {
                        it.username eq userVO.username
                    }
                }

                call.respond(HttpStatusCode.OK, userVO)
                return@patch
            } else {
                call.respond(HttpStatusCode.Unauthorized, ErrorMessage("cant find user"))
                return@patch
            }
        }

        get("/users/{id}/profile") {
            val userId = call.parameters["id"]!!.toInt()
            val user = database.sequenceOf(Tables.Users).find { it.id eq userId }!!
            val userProfile = UserProfileDto(
                user.username,
                user.email,
                user.country,
                user.city,
                user.age,
                user.gender,
                user.height,
                user.weight,
                user.climbingSince.toString(),
                user.imageUrl,
                user.creationDate.toString()
            )
            call.respond(HttpStatusCode.OK, userProfile)
            return@get
        }

        post("/{id}/photo") {
            val userId = call.parameters["id"]!!.toInt()
            val user = database.sequenceOf(Tables.Users).find { it.id eq userId }!!
            val multipart = call.receiveMultipart()
            var fileName: String? = null
// save elsewhere
            multipart.forEachPart { part ->
                if (part is PartData.FileItem) {
                    fileName = part.originalFileName as String
                    val fileBytes = part.streamProvider().readBytes()
                    File("uploads/$fileName").writeBytes(fileBytes)
                }
                part.dispose()
            }
            return@post
        }

        post("/{id}/route/log") {
            val routeId = call.request.queryParameters["route_id"]!!.secured().toInt()
            val userId = call.parameters["user_id"]!!.secured().toInt()
            val log = call.receive<Tables.RouteLog>()
            database.sequenceOf(Tables.RouteLogs).add(log)
            return@post
        }

        post("/{id}/boulder/log") {
            val routeId = call.request.queryParameters["boulder_id"]!!.secured().toInt()
            val userId = call.parameters["user_id"]!!.secured().toInt()
            val boulderLog = call.receive<Tables.BoulderLog>()
            database.sequenceOf(Tables.BoulderLogs).add(boulderLog)
            return@post
        }

        get("/{id}/routesLogs") {
            val userId = call.parameters["user_id"]?.toInt()!!

            val routeWithLog = database.sequenceOf(Tables.RouteLogs).filter { it.userId eq userId }.map { routeLog ->
                val route = database.sequenceOf(Tables.Routes).find { it.id eq routeLog.routeId }!!

                RouteLogWithRoute(
                    RouteLog(
                        routeLog.id,
                        routeLog.userId,
                        routeLog.routeId,
                        routeLog.routeName,
                        routeLog.text,
                        routeLog.rating,
                        routeLog.date.toString()
                    ),
                    com.example.data.model.Route(
                        route.id,
                        route.cragId,
                        route.cragName,
                        route.sectorId,
                        route.name,
                        route.grade,
                    )
                )
            }

            call.respond(
                HttpStatusCode.OK, routeWithLog
            )
        }

        get("/{id}/boulderLogs") {
            val userId = call.parameters["user_id"]?.toInt()!!

            val boulderWithLog =
                database.sequenceOf(Tables.BoulderLogs).filter { it.userId eq userId }.map { boulderLog ->
                    val route = database.sequenceOf(Tables.Routes).find { it.id eq boulderLog.id }!!

                    BoulderLogWithBoulder(
                        BoulderLog(
                            boulderLog.id,
                            boulderLog.userId,
                            boulderLog.boulderId,
                            boulderLog.boulderName,
                            boulderLog.text,
                            boulderLog.rating,
                            boulderLog.logDate.toString()
                        ),
                        Boulder(
                            route.id,
                            route.cragId,
                            route.cragName,
                            route.sectorId,
                            route.name,
                            route.grade,
                        )
                    )
                }

            call.respond(
                HttpStatusCode.OK, boulderWithLog
            )
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
                this.cragId = call.request.queryParameters["crag_id"]!!.secured().toInt()
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
            val spotId = call.request.queryParameters["crag_id"]?.secured()?.toInt()!!
            database.sequenceOf(Tables.UserFavCrags).removeIf { (it.userId eq userId) and (it.spotId eq spotId) }

            val spotsIds = database.from(Tables.UserFavCrags).select(Tables.UserFavCrags.spotId)
                .where { Tables.UserFavCrags.userId eq userId }.map { Tables.UserFavCrags.createEntity(it).cragId }

            call.respond(HttpStatusCode.OK, spotsIds)
        }

        post("/resetPassword") {
            val userMail = call.request.queryParameters["mail"]?.secured()!!.replace("%40", "@")
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
            email.setSmtpPort(465)
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