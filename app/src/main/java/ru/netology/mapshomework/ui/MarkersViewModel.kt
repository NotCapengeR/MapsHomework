package ru.netology.mapshomework.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.netology.mapshomework.core.db.marker.MarkerEntity
import ru.netology.mapshomework.core.repository.MarksRepository
import javax.inject.Inject

class MarkersViewModel @Inject constructor(
    private val repository: MarksRepository
) : ViewModel() {

    val marks: Flow<PagingData<MarkerEntity>> = repository.markers.cachedIn(viewModelScope)

    fun remove(id: Long) {
        viewModelScope.launch {
            repository.remove(id)
        }
    }

    fun edit(id: Long, newText: String) {
        viewModelScope.launch {
            repository.edit(id, newText)
        }
    }
}