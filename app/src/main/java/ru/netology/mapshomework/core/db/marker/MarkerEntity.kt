package ru.netology.mapshomework.core.db.marker

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "markers",
    indices =[Index("marker_id")]
)
data class MarkerEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "marker_id")
    val id: Long,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "latitude")
    val latitude: Double,
    @ColumnInfo(name = "longitude")
    val longitude: Double,
) {

    companion object {
        const val PAGE_SIZE: Int = 12
        const val MAX_SIZE: Int = 200
    }
}