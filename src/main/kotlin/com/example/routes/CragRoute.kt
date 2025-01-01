package com.example.routes

import com.example.DatabaseManager.database
import com.example.data.model.*
import com.example.data.model.CragDetailsDto
import com.example.data.model.CragDto
import com.example.data.model.Position
import com.example.data.model.Tables
import com.example.utils.PositionUtil.isCloserThanThreshold
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.Route
import org.ktorm.dsl.*
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

fun Route.crags() {

    route("/crags") {
        get("/area/{id}") {

            val id = call.parameters["id"]!!.toInt()
            val area = database.sequenceOf(Tables.Areas).find { it.id eq id }
            val crags = database.from(Tables.Crags).select(Tables.Crags.columns).where {
                Tables.Crags.areaId eq area!!.id.toString()
            }.map { row ->
                CragDto(
                    id = row[Tables.Crags.id] ?: 0,
                    areaId = row[Tables.Crags.areaId] ?: "0",
                    name = row[Tables.Crags.name] ?: "0",
                    latitude = row[Tables.Crags.latitude] ?: 0.0,
                    longitude = row[Tables.Crags.longitude] ?: 0.0,
                    thumbnailUrl = row[Tables.Crags.thumbnailUrl] ?: "0",
                )
            }.distinctBy { it.name }

            call.respond(
                HttpStatusCode.OK, crags
            )
        }

        get("/all") {
            val crags = database.from(Tables.Crags).select(Tables.Crags.columns).map { row ->
                CragDto(
                    id = row[Tables.Crags.id] ?: 0,
                    areaId = row[Tables.Crags.areaId] ?: "0",
                    name = row[Tables.Crags.name] ?: "0",
                    latitude = row[Tables.Crags.latitude] ?: 0.0,
                    longitude = row[Tables.Crags.longitude] ?: 0.0,
                    thumbnailUrl = row[Tables.Crags.thumbnailUrl] ?: "0",
                )
            }.distinctBy { it.name }
            call.respond(
                HttpStatusCode.OK, crags
            )
        }

        get("/{id}/details") {
            val id = call.parameters["id"]!!.toInt()
            val crag = database.sequenceOf(Tables.Crags).find { it.id eq id }
            val cragSectors = database.from(Tables.Sectors).select(Tables.Sectors.columns).where {
                Tables.Sectors.cragId eq crag!!.id.toString()
            }.map { sectorRows ->

                val parkings = database.from(Tables.Parkings).select(Tables.Parkings.columns).where {
                    Tables.Parkings.sectorId eq sectorRows[Tables.Sectors.id]!!
                }.map { parkingRows ->
                    ParkingDto(
                        id = parkingRows[Tables.Parkings.id] ?: 0,
                        sectorId = parkingRows[Tables.Parkings.sectorId] ?: 0,
                        name = parkingRows[Tables.Parkings.name] ?: "0",
                        latitude = parkingRows[Tables.Parkings.latitude] ?: 0.0,
                        longitude = parkingRows[Tables.Parkings.longitude] ?: 0.0,
                    )
                }

                val routes = database.from(Tables.Routes).select(Tables.Routes.columns).where {
                    Tables.Routes.sectorId eq sectorRows[Tables.Sectors.id]!!
                }.map { routeRows ->
                    RouteDto(
                        id = routeRows[Tables.Routes.id] ?: 0,
                        sectorId = routeRows[Tables.Routes.sectorId] ?: 0,
                        cragId = routeRows[Tables.Routes.cragId] ?: 0,
                        name = routeRows[Tables.Routes.name] ?: "0",
                        grade = routeRows[Tables.Routes.grade] ?: "0",
                    )
                }

                SectorDto(
                    id = sectorRows[Tables.Sectors.id] ?: 0,
                    cragId = sectorRows[Tables.Sectors.cragId] ?: "0",
                    name = sectorRows[Tables.Sectors.name] ?: "0",
                    routes = routes,
                    parkingSpots = parkings
                )
            }

            val models = database.from(Tables.Models).select(Tables.Models.columns).where {
                Tables.Models.parentCragId eq crag!!.id
            }.map { modelRows ->

                val sectorNames = database.from(Tables.ModelHasSectors).select(Tables.ModelHasSectors.columns).where {
                    Tables.ModelHasSectors.modelId eq modelRows[Tables.Models.id]!!
                }.map { modelHasSectorRows ->
                    val sector = database.sequenceOf(Tables.Sectors)
                        .find { it.id eq modelHasSectorRows[Tables.ModelHasSectors.sectorId]!! }
                    sector!!.name
                }

                ModelDto(
                    id = modelRows[Tables.Models.id].toString(),
                    parentCrag = modelRows[Tables.Models.parentCragId].toString(),
                    name = modelRows[Tables.Models.name] ?: "0",
                    sectorNames = sectorNames,
                    routesCount = modelRows[Tables.Models.routeCount] ?: 0
                )
            }

            val cragDetails = CragDetailsDto(
                id = crag!!.id,
                name = crag.name,
                latitude = crag.latitude,
                longitude = crag.longitude,
                thumbnailUrl = crag.thumbnailUrl,
                sectors = cragSectors,
                models = models
            )

            call.respond(
                HttpStatusCode.OK, cragDetails
            )
        }


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
            val lat = call.request.queryParameters["lat"]!!.secured().toDouble()
            val long = call.request.queryParameters["long"]!!.secured().toDouble()

//            In kilometers
            val threshold = 50
            val pos = Position(
                lat, long
            )

            val crags = database.from(Tables.Crags).select(Tables.Crags.columns).map { row ->
                CragDto(
                    id = row[Tables.Crags.id] ?: 0,
                    areaId = row[Tables.Crags.areaId] ?: "0",
                    name = row[Tables.Crags.name] ?: "0",
                    latitude = row[Tables.Crags.latitude] ?: 0.0,
                    longitude = row[Tables.Crags.longitude] ?: 0.0,
                    thumbnailUrl = row[Tables.Crags.thumbnailUrl] ?: "0",
                )
            }.filter { isCloserThanThreshold(it, pos, threshold) }.distinctBy { it.name }

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