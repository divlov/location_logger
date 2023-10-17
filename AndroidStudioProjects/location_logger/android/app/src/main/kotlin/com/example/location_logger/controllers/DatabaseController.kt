package com.example.location_logger.controllers

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase

import android.content.Context
import android.database.sqlite.SQLiteOpenHelper

class DatabaseController(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "LocationDatabase.db"
        private const val TABLE_LOCATIONS = "locations"
        private const val COLUMN_ID = "id"
        private const val COLUMN_LATITUDE = "latitude"
        private const val COLUMN_LONGITUDE = "longitude"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE $TABLE_LOCATIONS ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_LATITUDE REAL, $COLUMN_LONGITUDE REAL);"
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LOCATIONS")
        onCreate(db)
    }

    fun insertLocation(latitude: Double, longitude: Double): Long {
        val values = ContentValues()
        values.put(COLUMN_LATITUDE, latitude)
        values.put(COLUMN_LONGITUDE, longitude)

        val db = this.writableDatabase
        val id = db.insert(TABLE_LOCATIONS, null, values)
        db.close()

        return id
    }

    @SuppressLint("Range")
    fun getLastLocation(): String? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_LOCATIONS ORDER BY $COLUMN_ID DESC LIMIT 1", null)
        val location: String?

        if (cursor.moveToFirst()) {
            val latitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_LATITUDE))
            val longitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_LONGITUDE))
            location = "$latitude,$longitude"
        } else {
            location = null
        }

        cursor.close()
        db.close()

        return location
    }
}
