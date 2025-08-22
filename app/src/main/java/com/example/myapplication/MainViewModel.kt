package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class UiState {
    object Loading : UiState()
    data class Success(val users: List<User>) : UiState()
    data class Error(val message: String) : UiState()
}

class MainViewModel(private val repository: Repository) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    private val _selectedUser = MutableStateFlow<User?>(null)
    val selectedUser: StateFlow<User?> = _selectedUser

    fun fetchUsers() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = repository.getUsers()
            result
                .onSuccess { users ->
                    _uiState.value = UiState.Success(users.map { it.copy(isSelected = false) })
                }
                .onFailure { e ->
                    _uiState.value = UiState.Error(e.message ?: "Unknown error")
                }
        }
    }


    fun selectUser(selected: User) {
        val state = _uiState.value as? UiState.Success ?: return
        val updated = state.users.map { u ->


            u.copy(isSelected = (u.id == selected.id))
        }
        _uiState.value = UiState.Success(updated)
        _selectedUser.value = updated.firstOrNull { it.isSelected }
    }
}
