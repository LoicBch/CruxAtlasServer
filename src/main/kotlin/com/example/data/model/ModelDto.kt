package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ModelDto(
    val id: String,
    val name: String,
    val sectorNames: List<String>,
    val routesCount: Int,
    val downloadedDate: String? = null,
    val parentCrag: String = "",
    val size: Long = 0,
)