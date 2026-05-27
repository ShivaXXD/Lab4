package com.example.lab3

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProjectViewModel(private val repository: ProjectRepository) : ViewModel() {
    val projects: StateFlow<List<ProjectEntity>> = repository.projects.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun getProjectById(id: String) = repository.getProjectById(id)

    fun addProject(name: String, description: String) {
        viewModelScope.launch {
            repository.addProject(ProjectEntity(name = name, description = description))
        }
    }

    fun updateProject(project: ProjectEntity) {
        viewModelScope.launch { repository.updateProject(project) }
    }

    fun deleteProject(project: ProjectEntity) {
        viewModelScope.launch { repository.deleteProject(project) }
    }
}

class ProjectViewModelFactory(private val repository: ProjectRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProjectViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProjectViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}