package io.github.formula1.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.formula1.repository.SessionRepository
import io.github.formula1.view.SessionViewModel

class SessionViewModelFactory(private val repository: SessionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SessionViewModel::class.java)) {
            return SessionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}