package io.github.formula1.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.formula1.repository.StartingGridRepository
import io.github.formula1.view.StartingGridViewModel

class StartingGridViewModelFactory(private val repository: StartingGridRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StartingGridViewModel::class.java)) {
            return StartingGridViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}