package io.github.formula1.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.formula1.repository.DriverRepository
import io.github.formula1.view.DriverViewModel

class DriverViewModelFactory(private val repository: DriverRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DriverViewModel::class.java)) {
            return DriverViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}