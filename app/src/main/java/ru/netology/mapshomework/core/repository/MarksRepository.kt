package ru.netology.mapshomework.core.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.mapshomework.core.db.marker.MarkerDAO
import ru.netology.mapshomework.core.db.marker.MarkerEntity
import javax.inject.Inject
import javax.inject.Singleton

interface MarksRepository {

    val markers: Flow<PagingData<MarkerEntity>>

    suspend fun add(description: String, latitude: Double, longitude: Double): Boolean

    suspend fun edit(id: Long, newDescription: String): Boolean

    suspend fun remove(id: Long): Boolean

    suspend fun getById(id: Long): MarkerEntity?
}

@Singleton
class MarksRepositoryImpl @Inject constructor(
    private val dao: MarkerDAO
) : MarksRepository {

    override val markers: Flow<PagingData<MarkerEntity>> = Pager(
        config = PagingConfig(
            pageSize = MarkerEntity.PAGE_SIZE,
            prefetchDistance = (MarkerEntity.PAGE_SIZE - 3).coerceAtLeast(1),
            maxSize = MarkerEntity.MAX_SIZE
        ),
        pagingSourceFactory = dao::pagingSource
    ).flow


    override suspend fun add(description: String, latitude: Double, longitude: Double): Boolean {
        return (dao.add(description, latitude, longitude) > 0L)
    }


    override suspend fun edit(id: Long, newDescription: String): Boolean {
        return (dao.edit(id, newDescription) > 0)
    }

    override suspend fun remove(id: Long): Boolean {
        return (dao.delete(id) > 0)
    }

    override suspend fun getById(id: Long): MarkerEntity? = dao.getById(id)
}