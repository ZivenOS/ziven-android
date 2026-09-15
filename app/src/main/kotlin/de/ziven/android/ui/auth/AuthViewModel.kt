package de.ziven.android.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.ziven.android.data.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(authRepository.isLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.login(email, password)
                .onSuccess {
                    _isLoggedIn.value = true
                    _uiState.value = AuthUiState.Success
                    onSuccess()
                }
                .onFailure { _uiState.value = AuthUiState.Error(it.message ?: "Login failed") }
        }
    }

    fun register(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            authRepository.register(email, password)
                .onSuccess {
                    _isLoggedIn.value = true
                    _uiState.value = AuthUiState.Success
                    onSuccess()
                }
                .onFailure { _uiState.value = AuthUiState.Error(it.message ?: "Register failed") }
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            _isLoggedIn.value = false
            onComplete()
        }
    }

    fun consumeMessage() {
        _uiState.value = AuthUiState.Idle
    }
}

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data object Success : AuthUiState
    data class Error(val message: String) : AuthUiState
}
