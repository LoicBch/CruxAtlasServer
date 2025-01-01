package com.example.routes

import com.example.DatabaseManager
import com.example.data.model.*
import com.example.data.model.AreaDto
import com.example.data.model.Tables
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.Route
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import org.ktorm.dsl.*

fun List<CragDto>.convexHull(points: List<Pair<Double, Double>>): List<Pair<Double, Double>> {
    if (points.size < 3) return points

    val sortedPoints = points.sortedWith(compareBy({ it.first }, { it.second }))

    fun orientation(p: Pair<Double, Double>, q: Pair<Double, Double>, r: Pair<Double, Double>): Int {
        val value = (q.second - p.second) * (r.first - q.first) -
                (q.first - p.first) * (r.second - q.second)
        return when {
            value == 0.0 -> 0
            value > 0 -> 1
            else -> 2
        }
    }

    val lower = mutableListOf<Pair<Double, Double>>()
    for (point in sortedPoints) {
        while (lower.size >= 2 &&
            orientation(lower[lower.size - 2], lower[lower.size - 1], point) != 2
        ) {
            lower.removeAt(lower.size - 1)
        }
        lower.add(point)
    }

    val upper = mutableListOf<Pair<Double, Double>>()
    for (point in sortedPoints.asReversed()) {
        while (upper.size >= 2 &&
            orientation(upper[upper.size - 2], upper[upper.size - 1], point) != 2
        ) {
            upper.removeAt(upper.size - 1)
        }
        upper.add(point)
    }

    return (lower + upper.drop(1).dropLast(1))
}

fun List<Pair<Double, Double>>.serializePairs(): String {
    val jsonArray = this.map { JsonArray(listOf(JsonPrimitive(it.first), JsonPrimitive(it.second))) }
    return Json.encodeToString(JsonArray(jsonArray))
}

fun Route.areas() {

    route("/areas") {

        get("/updatePolygon"){

            val areaWithPolygon: MutableList<Pair<String, String>> = mutableListOf()

            val areas = DatabaseManager.database.from(Tables.Crags).select(Tables.Crags.columns).map { row ->
                CragDto(
                    id = row[Tables.Crags.id] ?: 0,
                    areaId = row[Tables.Crags.areaId] ?: "0",
                    name = row[Tables.Crags.name] ?: "0",
                    latitude = row[Tables.Crags.latitude] ?: 0.0,
                    longitude = row[Tables.Crags.longitude] ?: 0.0,
                    thumbnailUrl = row[Tables.Crags.thumbnailUrl] ?: "0",
                )
            }.groupBy { it.areaId }.filter { it.key != "0" }
                .map { entry ->
                    val points = entry.value.map { it.latitude to it.longitude }
                    val hull = entry.value.convexHull(points.filter { it.first != 0.0 && it.second != 0.0  })
                    val polygon = hull.serializePairs()
                    areaWithPolygon.add(entry.key to polygon)
                }

            areaWithPolygon.forEach { pair ->
                DatabaseManager.database.update(Tables.Areas) {
                    set(Tables.Areas.polygon, pair.second)
                    where { it.id eq pair.first.toInt() }
                }
            }

            call.respond(
                HttpStatusCode.OK, areas
            )
        }

        get("/all") {

            val areas = DatabaseManager.database.from(Tables.Areas).select(Tables.Areas.columns).map { row ->
                AreaDto(
                    id = row[Tables.Areas.id] ?: 0,
                    name = row[Tables.Areas.name] ?: "0",
                    country = row[Tables.Areas.country] ?: "0",
                    polygon = try {
                        val polygonJson = row[Tables.Areas.polygon] ?: "[]"
                        Json.decodeFromString<List<List<Double>>>(polygonJson)
                            .map { Position(it[0], it[1]) }
                    } catch (e: Exception) {
                        emptyList()
                    }
                )
            }.distinctBy { it.name }

            call.respond(
                HttpStatusCode.OK, areas
            )
        }
    }
}