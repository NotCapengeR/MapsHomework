package ru.netology.mapshomework.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.launch
import ru.netology.mapshomework.core.db.marker.MarkerEntity
import ru.netology.mapshomework.core.repository.MarksRepository
import javax.inject.Inject

class MapViewModel @Inject constructor(
    private val repository: MarksRepository
) : ViewModel() {

    val destination: MutableLiveData<MarkerEntity?> = MutableLiveData()


    fun addMark(description: String, latitude: Double, longitude: Double) {
        viewModelScope.launch {
            repository.add(description, latitude, longitude)
        }
    }

    fun addMark(description: String, point: Point) {
        addMark(description, point.latitude, point.longitude)
    }


    fun goToMark(id: Long) {
        viewModelScope.launch {
            destination.value = repository.getById(id)
        }
    }
}