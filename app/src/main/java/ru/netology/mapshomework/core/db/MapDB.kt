package ru.netology.mapshomework.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.netology.mapshomework.core.db.MapDB.Companion.DB_VERSION
import ru.netology.mapshomework.core.db.marker.MarkerDAO
import ru.netology.mapshomework.core.db.marker.MarkerEntity

@Database(
    entities = [MarkerEntity::class],
    version = DB_VERSION,
    exportSchema = true
)
abstract class MapDB : RoomDatabase() {

    abstract fun getMarkerDao(): MarkerDAO

    companion object {
        const val DB_VERSION: Int = 1
        const val DB_NAME: String = "map-database"
    }
}