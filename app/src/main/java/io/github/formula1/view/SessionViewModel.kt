package io.github.formula1.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.formula1.model.Resource
import io.github.formula1.model.dto.QualifierSessionResult
import io.github.formula1.model.dto.RaceSessionResult
import io.github.formula1.model.dto.SessionResponse
import io.github.formula1.model.dto.StartingGridResponse
import io.github.formula1.repository.SessionRepository
import kotlinx.coroutines.launch

class SessionViewModel(private val repository: SessionRepository) : ViewModel() {
    private val _sessions = MutableLiveData<Resource<List<SessionResponse>>>()
    private val _qualifyingSessions = MutableLiveData<Resource<List<QualifierSessionResult>>>()
    private val _raceSessions = MutableLiveData<Resource<List<RaceSessionResult>>>()


    val sessions: LiveData<Resource<List<SessionResponse>>> = _sessions
    val qualifyingSessions: LiveData<Resource<List<QualifierSessionResult>>> = _qualifyingSessions
    val raceSessions: LiveData<Resource<List<RaceSessionResult>>> = _raceSessions


    fun fetchSessions(currentYear: String) {
        _sessions.value = Resource.Loading()
        viewModelScope.launch {
            _sessions.value = repository.getCurrentYearSessions(currentYear)
        }
    }

    fun fetchPastSessions(currentYear: String) {
        _sessions.value = Resource.Loading()
        viewModelScope.launch {
            _sessions.value = repository.getCurrentYearPastSessions(currentYear)
        }
    }

    fun fetchQualifyingSessionResult(meetingKey: Int, sessionKey: Int) {
        _qualifyingSessions.value = Resource.Loading()
        viewModelScope.launch {
            _qualifyingSessions.value = repository.getQualifyingSessionResult(meetingKey, sessionKey)
        }
    }

    fun fetchRaceSessionResult(meetingKey: Int, sessionKey: Int) {
        _raceSessions.value = Resource.Loading()
        viewModelScope.launch {
            _raceSessions.value = repository.getRaceSessionResult(meetingKey, sessionKey)
        }
    }
}