package com.example.data.model

sealed class Response<T> {
    class Success<T>(val value: T) : Response<T>()
    class Invalid(exceptions: CustomExceptions)
}
