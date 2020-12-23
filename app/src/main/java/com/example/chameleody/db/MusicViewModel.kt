package com.example.chameleody.db

import androidx.lifecycle.*
import com.example.chameleody.model.MusicFile
import kotlinx.coroutines.launch

class MusicViewModel(private val repository: MusicRepository): ViewModel() {
    val allMusics: LiveData<List<MusicFile>> = repository.allMusics.asLiveData()

    fun insert(musicFile: MusicFile) = viewModelScope.launch {
        repository.insert(musicFile)
    }
}