package de.ziven.android.ui.pantry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.ziven.android.data.pantry.PantryRepository
import de.ziven.shared.model.PantryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PantryViewModel @Inject constructor(
    private val repository: PantryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<PantryUiState>(PantryUiState.Loading)
    val uiState: StateFlow<PantryUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = PantryUiState.Loading
            try {
                val items = repository.list().sortedBy { it.daysToExpiry ?: Int.MAX_VALUE }
                _uiState.value = PantryUiState.Success(items)
            } catch (e: Exception) {
                _uiState.value = PantryUiState.Error(e.message ?: "Could not load pantry")
            }
        }
    }

    fun discard(item: PantryItem) {
        viewModelScope.launch {
            try {
                repository.discard(item.id)
                load()
            } catch (e: Exception) {
                _uiState.value = PantryUiState.Error(e.message ?: "Could not discard item")
            }
        }
    }

    fun delete(item: PantryItem) {
        viewModelScope.launch {
            try {
                repository.delete(item.id)
                load()
            } catch (e: Exception) {
                _uiState.value = PantryUiState.Error(e.message ?: "Could not delete item")
            }
        }
    }

    fun addProduct(barcode: String, productName: String, brand: String?, quantity: Double, unit: String) {
        viewModelScope.launch {
            try {
                repository.addProduct(barcode, productName, brand, quantity, unit)
                load()
            } catch (e: Exception) {
                _uiState.value = PantryUiState.Error(e.message ?: "Could not add product")
            }
        }
    }
}

sealed interface PantryUiState {
    data object Loading : PantryUiState
    data class Success(val items: List<PantryItem>) : PantryUiState
    data class Error(val message: String) : PantryUiState
}
