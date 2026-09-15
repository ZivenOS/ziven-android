package de.ziven.android.ui.cook

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.ziven.android.data.plan.PlanRepository
import de.ziven.shared.model.CookSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CookViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: PlanRepository,
) : ViewModel() {

    private val slotId: String = savedStateHandle["slotId"] ?: error("slotId required")

    private val _uiState = MutableStateFlow<CookUiState>(CookUiState.Loading)
    val uiState: StateFlow<CookUiState> = _uiState.asStateFlow()

    init {
        startCook()
    }

    fun startCook() {
        viewModelScope.launch {
            _uiState.value = CookUiState.Loading
            try {
                val cached = repository.cachedCookSession()
                val session = repository.startCook(slotId)
                repository.saveCookSession(session)
                _uiState.value = CookUiState.Success(session)
            } catch (e: Exception) {
                val cached = repository.cachedCookSession()
                if (cached != null) {
                    _uiState.value = CookUiState.Success(cached)
                } else {
                    _uiState.value = CookUiState.Error(e.message ?: "Could not start cook session")
                }
            }
        }
    }

    fun complete() {
        viewModelScope.launch {
            val current = _uiState.value as? CookUiState.Success ?: return@launch
            _uiState.value = CookUiState.Loading
            try {
                val completed = repository.completeCook(current.session.id)
                repository.saveCookSession(completed)
                _uiState.value = CookUiState.Done(completed)
            } catch (e: Exception) {
                _uiState.value = CookUiState.Error(e.message ?: "Could not complete session")
            }
        }
    }
}

sealed interface CookUiState {
    data object Loading : CookUiState
    data class Success(val session: CookSession) : CookUiState
    data class Done(val session: CookSession) : CookUiState
    data class Error(val message: String) : CookUiState
}
