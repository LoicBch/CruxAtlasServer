package com.example.data.model

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
class RouteLogWithRoute(
    var log: RouteLog,
    var route: Route
)

@Serializable
class RouteLog(
    var id: Int,
    var userId: Int,
    var routeId: Int,
    var routeName: String,
    var text: String,
    var rating: String,
    var date: String
)