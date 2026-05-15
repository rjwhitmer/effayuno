package io.github.formula1.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.formula1.model.Resource
import io.github.formula1.model.dto.TeamChampionshipResponse
import io.github.formula1.repository.TeamChampionshipRepository
import kotlinx.coroutines.launch

class TeamChampionshipViewModel(private val repository: TeamChampionshipRepository) : ViewModel() {
    private val _teams = MutableLiveData<Resource<List<TeamChampionshipResponse>>>()
    val teams: LiveData<Resource<List<TeamChampionshipResponse>>> = _teams

    fun fetchTeamChampionships() {
        _teams.value = Resource.Loading()
        viewModelScope.launch {
            _teams.value = repository.getTeamChampionships()
        }
    }
}