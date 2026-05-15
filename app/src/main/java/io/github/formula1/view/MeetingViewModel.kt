package io.github.formula1.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.formula1.model.Resource
import io.github.formula1.model.dto.MeetingResponse
import io.github.formula1.repository.MeetingRepository
import kotlinx.coroutines.launch

class MeetingViewModel(private val repository: MeetingRepository) : ViewModel() {
    private val _meetings = MutableLiveData<Resource<List<MeetingResponse>>>()
    public val meetings: LiveData<Resource<List<MeetingResponse>>> = _meetings

    fun fetchMeetings(currentYear: String) {
        _meetings.value = Resource.Loading()
        viewModelScope.launch {
            _meetings.value = repository.getMeetings(currentYear)
        }
    }
}