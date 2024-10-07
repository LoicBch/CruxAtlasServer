package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    val username: String,
    val email: String,
    val country: String,
    val city: String,
    val age: Int,
    val gender: String,
    val height: Int,
    val weight: Int,
    val climbingSince: String,
    var imageUrl: String?,
    val creationDate: String,
)