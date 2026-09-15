package de.ziven.android.ui.plan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.ziven.android.data.plan.PlanRepository
import de.ziven.android.ui.common.mondayIsoOf
import de.ziven.shared.model.PlanSlot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanViewModel @Inject constructor(
    private val repository: PlanRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlanUiState>(PlanUiState.Loading)
    val uiState: StateFlow<PlanUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load(weekStart: String = mondayIsoOf()) {
        viewModelScope.launch {
            _uiState.value = PlanUiState.Loading
            try {
                val plan = repository.getPlan(weekStart)
                val grouped = plan.slots.groupBy { it.day }.toSortedMap()
                _uiState.value = PlanUiState.Success(weekStart, grouped)
            } catch (e: Exception) {
                _uiState.value = PlanUiState.Error(e.message ?: "Could not load plan")
            }
        }
    }

    fun generate(weekStart: String = mondayIsoOf()) {
        viewModelScope.launch {
            try {
                repository.generate(weekStart)
                load(weekStart)
            } catch (e: Exception) {
                _uiState.value = PlanUiState.Error(e.message ?: "Generate failed")
            }
        }
    }
}

sealed interface PlanUiState {
    data object Loading : PlanUiState
    data class Success(val weekStart: String, val days: Map<Int, List<PlanSlot>>) : PlanUiState
    data class Error(val message: String) : PlanUiState
}
