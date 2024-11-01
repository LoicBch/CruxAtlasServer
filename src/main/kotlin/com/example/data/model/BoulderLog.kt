package com.example.data.model

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
class BoulderLogWithBoulder(
    var boulderLog: BoulderLog,
    var boulder: Boulder
)

@Serializable
class BoulderLog(
    var id: Int,
    var userId: Int,
    var boulderId: Int,
    var boulderName: String,
    var text: String,
    var rating: String,
    var logDate: String
)
