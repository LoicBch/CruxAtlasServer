package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
class CragDetailsDto(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val thumbnailUrl: String,
    val sectors: List<SectorDto>,
    val models: List<ModelDto>
)

