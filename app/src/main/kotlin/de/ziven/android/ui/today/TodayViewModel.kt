package de.ziven.android.ui.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.ziven.android.data.plan.PlanRepository
import de.ziven.android.ui.common.isoDayOfWeek
import de.ziven.android.ui.common.mondayIsoOf
import de.ziven.android.ui.common.todayIso
import de.ziven.shared.model.PlanSlot
import de.ziven.shared.model.PlanView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodayViewModel @Inject constructor(
    private val repository: PlanRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<TodayUiState>(TodayUiState.Loading)
    val uiState: StateFlow<TodayUiState> = _uiState.asStateFlow()

    init {
        loadToday()
    }

    fun loadToday() {
        viewModelScope.launch {
            _uiState.value = TodayUiState.Loading
            try {
                val weekStart = mondayIsoOf()
                val today = todayIso()
                val cached = repository.cachedPlan()
                cached?.let { emitToday(it, today) }
                val plan = repository.getPlan(weekStart)
                repository.savePlan(plan)
                emitToday(plan, today)
            } catch (e: Exception) {
                if (_uiState.value !is TodayUiState.Success) {
                    _uiState.value = TodayUiState.Error(e.message ?: "Could not load today")
                }
            }
        }
    }

    private fun emitToday(plan: PlanView, today: String) {
        val todaySlots = plan.slots
            .filter { isoDayOfWeek(it.weekStart) + it.day == isoDayOfWeek(today) }
            .sortedWith(slotOrder)
        _uiState.value = TodayUiState.Success(today, todaySlots)
    }

    fun generateWeek() {
        viewModelScope.launch {
            val current = _uiState.value
            _uiState.value = TodayUiState.Loading
            try {
                repository.generate(mondayIsoOf())
                loadToday()
            } catch (e: Exception) {
                _uiState.value = if (current is TodayUiState.Success) current.copy(message = e.message)
                else TodayUiState.Error(e.message ?: "Generate failed")
            }
        }
    }

    private val slotOrder = Comparator<PlanSlot> { a, b ->
        val order = listOf("breakfast", "lunch", "dinner", "snack")
        order.indexOf(a.slot).compareTo(order.indexOf(b.slot))
    }
}

sealed interface TodayUiState {
    data object Loading : TodayUiState
    data class Success(val date: String, val slots: List<PlanSlot>, val message: String? = null) : TodayUiState
    data class Error(val message: String) : TodayUiState
}
