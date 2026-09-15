package de.ziven.android.ui.barcode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.ziven.android.data.pantry.PantryRepository
import de.ziven.shared.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BarcodeViewModel @Inject constructor(
    private val repository: PantryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<BarcodeUiState>(BarcodeUiState.Scanning)
    val uiState: StateFlow<BarcodeUiState> = _uiState.asStateFlow()

    fun onBarcode(code: String) {
        if (_uiState.value !is BarcodeUiState.Scanning) return
        _uiState.value = BarcodeUiState.Loading(code)
        viewModelScope.launch {
            try {
                val product = repository.lookupBarcode(code)
                _uiState.value = BarcodeUiState.Found(code, product)
            } catch (e: Exception) {
                // Not found or OFF error — let the user enter details manually.
                _uiState.value = BarcodeUiState.Manual(code)
            }
        }
    }

    fun addProduct(quantity: Double, unit: String) {
        val current = _uiState.value as? BarcodeUiState.Found ?: return
        viewModelScope.launch {
            _uiState.value = BarcodeUiState.Loading(current.code)
            try {
                repository.addProduct(current.code, current.product.name, current.product.brand, quantity, unit)
                _uiState.value = BarcodeUiState.Added
            } catch (e: Exception) {
                _uiState.value = BarcodeUiState.Error(e.message ?: "Could not add product")
            }
        }
    }

    fun addManual(barcode: String, name: String, brand: String?, quantity: Double, unit: String) {
        viewModelScope.launch {
            _uiState.value = BarcodeUiState.Loading(barcode)
            try {
                repository.addProduct(barcode, name, brand, quantity, unit)
                _uiState.value = BarcodeUiState.Added
            } catch (e: Exception) {
                _uiState.value = BarcodeUiState.Error(e.message ?: "Could not add product")
            }
        }
    }

    fun reset() {
        _uiState.value = BarcodeUiState.Scanning
    }
}

sealed interface BarcodeUiState {
    data object Scanning : BarcodeUiState
    data class Loading(val code: String) : BarcodeUiState
    data class Found(val code: String, val product: Product) : BarcodeUiState
    data class Manual(val code: String) : BarcodeUiState
    data object Added : BarcodeUiState
    data class Error(val message: String) : BarcodeUiState
}
