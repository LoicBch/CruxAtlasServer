package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ErrorMessage(
    val message: String
)