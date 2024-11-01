package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
class Route(
    var id: Int,
    var cragId: Int,
    var cragName: String,
    var sectorId: Int,
    var name: String,
    var grade: String
)