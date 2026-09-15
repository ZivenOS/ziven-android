package de.ziven.android.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.ziven.android.data.shop.ShopRepository
import de.ziven.android.ui.common.mondayIsoOf
import de.ziven.shared.model.ShopItem
import de.ziven.shared.model.ShoppingList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val repository: ShopRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ListUiState>(ListUiState.Loading)
    val uiState: StateFlow<ListUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = ListUiState.Loading
            try {
                val list = repository.getList(mondayIsoOf())
                _uiState.value = ListUiState.Success(list)
            } catch (e: Exception) {
                _uiState.value = ListUiState.Error(e.message ?: "Could not load list")
            }
        }
    }

    fun generate() {
        viewModelScope.launch {
            _uiState.value = ListUiState.Loading
            try {
                val list = repository.generate(mondayIsoOf())
                _uiState.value = ListUiState.Success(list)
            } catch (e: Exception) {
                _uiState.value = ListUiState.Error(e.message ?: "Could not generate list")
            }
        }
    }

    fun toggle(item: ShopItem) {
        viewModelScope.launch {
            try {
                repository.check(item.id, !item.checked)
                // optimistic: update local copy
                val current = _uiState.value as? ListUiState.Success ?: return@launch
                val updated = current.list.items.map {
                    if (it.id == item.id) it.copy(checked = !item.checked) else it
                }
                _uiState.value = current.copy(list = current.list.copy(items = updated))
            } catch (e: Exception) {
                _uiState.value = ListUiState.Error(e.message ?: "Could not update item")
            }
        }
    }

    fun shareText(): String {
        val current = _uiState.value as? ListUiState.Success ?: return ""
        val lines = current.list.items
            .groupBy { it.category }
            .flatMap { (category, items) ->
                listOf(category.uppercase()) +
                    items.sortedBy { it.checked }.map { "${if (it.checked) "[x]" else "[ ]"} ${it.name.de} ${formatQuantity(it.quantity)} ${it.unit}" }
            }
        return lines.joinToString("\n")
    }

    private fun formatQuantity(q: Double): String =
        if (q == q.toLong().toDouble()) q.toLong().toString() else q.toString()
}

sealed interface ListUiState {
    data object Loading : ListUiState
    data class Success(val list: ShoppingList) : ListUiState
    data class Error(val message: String) : ListUiState
}
