package ru.netology.mapshomework.core.db.marker

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query

@Dao
interface MarkerDAO {

    @Query("SELECT * FROM markers ORDER BY marker_id DESC")
    fun pagingSource(): PagingSource<Int, MarkerEntity>

    @Query("INSERT OR IGNORE INTO markers(description, latitude, longitude) VALUES(:description, :latitude, :longitude)")
    suspend fun add(description: String, latitude: Double, longitude: Double): Long

    @Query("UPDATE markers SET description = :description WHERE marker_id = :id")
    suspend fun edit(id: Long, description: String): Int

    @Query("DELETE FROM markers WHERE marker_id = :id")
    suspend fun delete(id: Long): Int

    @Query("SELECT * FROM markers WHERE marker_id = :id LIMIT 1")
    suspend fun getById(id: Long): MarkerEntity?
}