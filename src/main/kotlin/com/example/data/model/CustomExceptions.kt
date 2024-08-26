package com.example.data.model

sealed class CustomExceptions{
    class EntityNotFoundException( val internalCode : Int = 20 , val message : String): CustomExceptions()
}
