package de.ziven.android.ui.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.ziven.android.data.account.AccountRepository
import de.ziven.android.data.household.HouseholdRepository
import de.ziven.android.data.nutrition.NutritionRepository
import de.ziven.android.ui.common.mondayIsoOf
import de.ziven.shared.model.Household
import de.ziven.shared.model.HouseholdMember
import de.ziven.shared.model.NutritionWeek
import de.ziven.shared.model.ProfilePatch
import de.ziven.shared.model.WeightEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoreViewModel @Inject constructor(
    private val householdRepository: HouseholdRepository,
    private val nutritionRepository: NutritionRepository,
    private val accountRepository: AccountRepository,
) : ViewModel() {

    private val _householdState = MutableStateFlow<HouseholdUiState>(HouseholdUiState.Loading)
    val householdState: StateFlow<HouseholdUiState> = _householdState.asStateFlow()

    private val _nutritionState = MutableStateFlow<NutritionUiState>(NutritionUiState.Loading)
    val nutritionState: StateFlow<NutritionUiState> = _nutritionState.asStateFlow()

    private val _weightState = MutableStateFlow<WeightUiState>(WeightUiState.Loading)
    val weightState: StateFlow<WeightUiState> = _weightState.asStateFlow()

    private val _eventState = MutableStateFlow<MoreEvent?>(null)
    val eventState: StateFlow<MoreEvent?> = _eventState.asStateFlow()

    init { loadHousehold() }

    fun loadHousehold() {
        viewModelScope.launch {
            _householdState.value = HouseholdUiState.Loading
            try {
                val household = householdRepository.current()
                _householdState.value = HouseholdUiState.Success(household)
            } catch (e: Exception) {
                _householdState.value = HouseholdUiState.Error(e.message ?: "Could not load household")
            }
        }
    }

    fun loadNutrition() {
        viewModelScope.launch {
            _nutritionState.value = NutritionUiState.Loading
            try {
                val week = nutritionRepository.getWeek(mondayIsoOf())
                _nutritionState.value = NutritionUiState.Success(week)
            } catch (e: Exception) {
                _nutritionState.value = NutritionUiState.Error(e.message ?: "Could not load nutrition")
            }
        }
    }

    fun loadWeight(memberId: String) {
        viewModelScope.launch {
            _weightState.value = WeightUiState.Loading
            try {
                val entries = householdRepository.weightSeries(memberId)
                _weightState.value = WeightUiState.Success(entries)
            } catch (e: Exception) {
                _weightState.value = WeightUiState.Error(e.message ?: "Could not load weight")
            }
        }
    }

    fun updateProfile(memberId: String, patch: ProfilePatch) {
        viewModelScope.launch {
            try {
                householdRepository.updateProfile(memberId, patch)
                _eventState.value = MoreEvent.Message("Profil gespeichert")
                loadHousehold()
            } catch (e: Exception) {
                _eventState.value = MoreEvent.Message(e.message ?: "Could not save profile")
            }
        }
    }

    fun invite(email: String, role: String) {
        viewModelScope.launch {
            try {
                householdRepository.invite(email, role)
                _eventState.value = MoreEvent.Message("Einladung gesendet")
                loadHousehold()
            } catch (e: Exception) {
                _eventState.value = MoreEvent.Message(e.message ?: "Could not send invite")
            }
        }
    }

    fun logWeight(memberId: String, kg: Double, loggedOn: String? = null) {
        viewModelScope.launch {
            try {
                householdRepository.logWeight(memberId, kg, loggedOn)
                loadWeight(memberId)
            } catch (e: Exception) {
                _eventState.value = MoreEvent.Message(e.message ?: "Could not log weight")
            }
        }
    }

    fun exportAccount(onJson: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val export = accountRepository.export()
                onJson(export.toString())
            } catch (e: Exception) {
                _eventState.value = MoreEvent.Message(e.message ?: "Export failed")
            }
        }
    }

    fun deleteAccount(password: String, onDeleted: () -> Unit) {
        viewModelScope.launch {
            try {
                accountRepository.delete(password)
                onDeleted()
            } catch (e: Exception) {
                _eventState.value = MoreEvent.Message(e.message ?: "Could not delete account")
            }
        }
    }

    fun clearEvent() { _eventState.value = null }
}

sealed interface HouseholdUiState {
    data object Loading : HouseholdUiState
    data class Success(val household: Household) : HouseholdUiState
    data class Error(val message: String) : HouseholdUiState
}

sealed interface NutritionUiState {
    data object Loading : NutritionUiState
    data class Success(val week: NutritionWeek, val useKj: Boolean = false) : NutritionUiState
    data class Error(val message: String) : NutritionUiState
}

sealed interface WeightUiState {
    data object Loading : WeightUiState
    data class Success(val entries: List<WeightEntry>) : WeightUiState
    data class Error(val message: String) : WeightUiState
}

sealed interface MoreEvent {
    data class Message(val text: String) : MoreEvent
}

private fun NutritionWeek.toDisplayString(useKj: Boolean): String =
    this.toString() // placeholder; not used directly
