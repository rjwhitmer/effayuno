package io.github.formula1.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.formula1.repository.TeamChampionshipRepository
import io.github.formula1.view.TeamChampionshipViewModel

class TeamChampionshipViewModelFactory(private val repository: TeamChampionshipRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TeamChampionshipViewModel::class.java)) {
            return TeamChampionshipViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}