package com.example.data.model

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
class BoulderLog(
    var id: Int,
    var userId: Int,
    var boulderId: Int,
    var text: String,
    var rating: String,
    var logDate: String
)
