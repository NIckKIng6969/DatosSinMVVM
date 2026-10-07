package com.example.datossinmvvm

// Modelo de la tabla User (lo que en Room seria la @Entity)
data class User(
    val uid: Int = 0,
    val firstName: String?,
    val lastName: String?
)
