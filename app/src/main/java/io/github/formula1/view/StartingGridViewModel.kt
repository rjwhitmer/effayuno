package io.github.formula1.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.formula1.model.Resource
import io.github.formula1.model.dto.StartingGridResponse
import io.github.formula1.repository.StartingGridRepository
import kotlinx.coroutines.launch

class StartingGridViewModel(private val repository: StartingGridRepository) : ViewModel() {
    private val _polePosition = MutableLiveData<Resource<List<StartingGridResponse>>>()
    val polePosition: LiveData<Resource<List<StartingGridResponse>>> = _polePosition

    fun fetchPolePosition(meetingKey: Int, position: Int) {
        _polePosition.value = Resource.Loading()
        viewModelScope.launch {
            _polePosition.value = repository.getPolePosition(meetingKey, position)
        }
    }
}