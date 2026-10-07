package com.example.datossinmvvm

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Esta clase hace el papel del DAO de Room: aqui estan las consultas SQL.
class UserDao(context: Context) : SQLiteOpenHelper(context, "user_db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE User (" +
                "uid INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "first_name TEXT, " +
                "last_name TEXT)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS User")
        onCreate(db)
    }

    fun insert(user: User) {
        val valores = ContentValues().apply {
            put("first_name", user.firstName)
            put("last_name", user.lastName)
        }
        writableDatabase.insert("User", null, valores)
    }

    fun getAll(): List<User> {
        val lista = mutableListOf<User>()
        val cursor = readableDatabase.rawQuery("SELECT uid, first_name, last_name FROM User ORDER BY uid", null)
        while (cursor.moveToNext()) {
            lista.add(User(cursor.getInt(0), cursor.getString(1), cursor.getString(2)))
        }
        cursor.close()
        return lista
    }

    // Ejercicio 1: obtiene el ultimo registro agregado
    fun getLast(): User? {
        var user: User? = null
        val cursor = readableDatabase.rawQuery(
            "SELECT uid, first_name, last_name FROM User ORDER BY uid DESC LIMIT 1", null
        )
        if (cursor.moveToFirst()) {
            user = User(cursor.getInt(0), cursor.getString(1), cursor.getString(2))
        }
        cursor.close()
        return user
    }

    fun delete(user: User) {
        writableDatabase.delete("User", "uid = ?", arrayOf(user.uid.toString()))
    }

    // Ejercicio 2: CRUD completo
    fun getById(id: Int): User? {
        var user: User? = null
        val cursor = readableDatabase.rawQuery(
            "SELECT uid, first_name, last_name FROM User WHERE uid = ?", arrayOf(id.toString())
        )
        if (cursor.moveToFirst()) {
            user = User(cursor.getInt(0), cursor.getString(1), cursor.getString(2))
        }
        cursor.close()
        return user
    }

    fun update(id: Int, nombre: String, apellido: String) {
        val valores = ContentValues().apply {
            put("first_name", nombre)
            put("last_name", apellido)
        }
        writableDatabase.update("User", valores, "uid = ?", arrayOf(id.toString()))
    }

    fun deleteById(id: Int) {
        writableDatabase.delete("User", "uid = ?", arrayOf(id.toString()))
    }
}
