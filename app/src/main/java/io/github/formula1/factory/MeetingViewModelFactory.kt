package io.github.formula1.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.formula1.repository.MeetingRepository
import io.github.formula1.view.MeetingViewModel

class MeetingViewModelFactory(private val repository: MeetingRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MeetingViewModel::class.java)) {
            return MeetingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}