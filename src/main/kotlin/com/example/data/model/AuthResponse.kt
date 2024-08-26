package com.example.data.model

@kotlinx.serialization.Serializable
data class AuthResponse(
    val token: String,
    val user: UserDto
)