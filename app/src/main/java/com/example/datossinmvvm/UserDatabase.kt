package com.example.datossinmvvm

import android.content.Context

// En Room esto seria la clase @Database. Aqui solo devuelve el DAO.
class UserDatabase private constructor(context: Context) {
    val dao: UserDao = UserDao(context)

    companion object {
        @Volatile
        private var instancia: UserDatabase? = null

        fun getInstance(context: Context): UserDatabase {
            return instancia ?: synchronized(this) {
                instancia ?: UserDatabase(context).also { instancia = it }
            }
        }
    }
}
