package com.example.data.model

import com.example.DatabaseManager
import io.konform.validation.Validation
import io.konform.validation.ValidationResult
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength
import kotlinx.serialization.Serializable
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf

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
    val cragId: String,
    val sectorId: String,
    val name: String,
    val grade: Double,
)
