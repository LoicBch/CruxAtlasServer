package com.example.data.model

@kotlinx.serialization.Serializable
data class AuthRequest(val username : String , val password: String)
