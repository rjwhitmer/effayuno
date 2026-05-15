package io.github.formula1.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.formula1.model.Resource
import io.github.formula1.model.dto.DriverChampionship
import io.github.formula1.model.dto.DriverResponse
import io.github.formula1.repository.DriverRepository
import kotlinx.coroutines.launch
import java.sql.Driver

class DriverViewModel(private val repository: DriverRepository) : ViewModel() {
    private val _drivers = MutableLiveData<Resource<List<DriverResponse>>>()
    val drivers: LiveData<Resource<List<DriverResponse>>> = _drivers
    private val _driverChampionships = MutableLiveData<Resource<List<DriverChampionship>>>()
    val driverChampionships: LiveData<Resource<List<DriverChampionship>>> = _driverChampionships

    fun fetchDrivers() {
        _drivers.value = Resource.Loading()
        viewModelScope.launch {
            _drivers.value = repository.getDrivers()
        }
    }

    fun fetchDriverChampionships() {
        _driverChampionships.value = Resource.Loading()
        viewModelScope.launch {
            _driverChampionships.value = repository.getDriverChampionships()
        }
    }

    fun fetchDriversSessionSpecific(sessionKey: Int, meetingKey: Int) {
        _drivers.value = Resource.Loading()
        viewModelScope.launch {
            _drivers.value = repository.getDriversSessionSpecific(sessionKey, meetingKey)
        }
    }
}