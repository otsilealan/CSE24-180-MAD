package com.accommodation.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accommodation.data.database.entities.User
import com.accommodation.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(private val repo: UserRepository) : ViewModel() {
    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state

    fun register(email: String, password: String, studentId: String?, phone: String, role: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            _state.value = repo.register(email, password, studentId, phone, role)
                .fold({ AuthState.Success(it) }, { AuthState.Error(it.message ?: "Registration failed") })
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            _state.value = repo.login(email, password)
                .fold({ AuthState.Success(it) }, { AuthState.Error(it.message ?: "Login failed") })
        }
    }

    fun reset() { _state.value = AuthState.Idle }
}
