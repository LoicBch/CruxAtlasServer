package com.example.routes

import com.example.DatabaseManager.database
import com.example.data.model.CragDto
import com.example.data.model.Position
import com.example.data.model.Tables
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.dsl.map
import org.ktorm.dsl.select
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf

fun Route.crags() {

    route("/crags") {

        get("/{id}") {
            val id = call.parameters["id"]
            if (!id.isNullOrEmpty()) {
                val spotId = call.parameters["id"]!!.toInt()
                val crag = database.sequenceOf(Tables.Crags).find { it.id eq spotId }

                val cragDto = CragDto(
                    id = crag?.id ?: 0,
                    areaId = crag?.areaId ?: "0",
                    name = crag?.name ?: "0",
                    latitude = crag?.latitude ?: 0.0,
                    longitude = crag?.longitude ?: 0.0,
                    thumbnailUrl = crag?.thumbnailUrl ?: "0"
                )

                crag?.let {
                    call.respond(
                        HttpStatusCode.OK, cragDto
                    )
                } ?: run {
                    call.respond(
                        HttpStatusCode.BadRequest, "Spot inexistant"
                    )
                }
            } else {
                call.respond(
                    HttpStatusCode.BadRequest, "Spot inexistant"
                )
            }
        }

        get {
            val userId = call.request.queryParameters["user_id"]?.secured()
//            val filter = call.request.queryParameters["filter"]?.secured()
            val lat = call.request.queryParameters["lat"]!!.secured().toDouble()
            val long = call.request.queryParameters["long"]!!.secured().toDouble()
            val threshold = 10

            val pos = Position(
                lat, long
            )

            val crags = database.from(Tables.Crags)
                .select(Tables.Crags.columns)
                .map { row ->
                    CragDto(
                        id = row[Tables.Crags.id] ?: 0,
                        areaId = row[Tables.Crags.areaId] ?: "0",
                        name = row[Tables.Crags.name] ?: "0",
                        latitude = row[Tables.Crags.latitude] ?: 0.0,
                        longitude = row[Tables.Crags.longitude] ?: 0.0,
                        thumbnailUrl = row[Tables.Crags.thumbnailUrl] ?: "0",
                    )
                }.distinctBy { it.name }

//            if (filter != null) {
//                spots = spots.filter {
//                    filter.split(",").contains(it.type.lowercase())
//                }
//            }

            call.respond(
                HttpStatusCode.OK, crags
            )
        }
    }
}