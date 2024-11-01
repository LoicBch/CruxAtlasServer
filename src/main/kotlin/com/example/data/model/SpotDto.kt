package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Int,
    val username: String,
    val password: String,
    val email: String,
    val country: String,
    val city: String,
    val age: Int,
    val gender: String,
    val weight: Int,
    val height: Int,
    val climbingSince: String,
    val imageUrl: String?,
    val isSubscribe: Boolean = false,
)

@Serializable
data class CragDto(
    val id: Int,
    val areaId: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val thumbnailUrl: String,
)

fun Tables.Crag.toDto(): CragDto = CragDto(
    id = this.id,
    areaId = areaId,
    name = this.name,
    latitude = this.latitude,
    longitude = this.longitude,
    thumbnailUrl = this.thumbnailUrl
)

@Serializable
data class SectorDto(
    val id: Int,
    val cragId: String,
    val name: String,
    val routes: List<RouteDto>,
    val parkingSpots: List<ParkingDto>
)

@Serializable
data class AreaDto(
    val id: Int,
    val name: String,
    val country: String,
)

@Serializable
data class RouteDto(
    val id: Int,
    val cragId: Int,
    val sectorId: Int,
    val name: String,
    val grade: String,
)

@Serializable
data class ParkingDto(
    val id: Int,
    val sectorId: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
)
