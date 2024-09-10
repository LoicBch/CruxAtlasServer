package com.example.data.model

@kotlinx.serialization.Serializable
class NewsItem(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String
)