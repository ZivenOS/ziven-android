package de.ziven.android.ui.preview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.ziven.android.data.preview.ImportQueue
import de.ziven.android.data.preview.PreviewRepository
import de.ziven.android.ui.common.mondayIsoOf
import de.ziven.shared.model.PreviewBundle
import de.ziven.shared.model.PreviewProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreviewViewModel @Inject constructor(
    private val repository: PreviewRepository,
    private val importQueue: ImportQueue,
) : ViewModel() {

    private val _uiState = MutableStateFlow<PreviewUiState>(PreviewUiState.Idle)
    val uiState: StateFlow<PreviewUiState> = _uiState.asStateFlow()

    fun generatePreview() {
        viewModelScope.launch {
            _uiState.value = PreviewUiState.Loading
            try {
                val bundle = repository.previewPlan(mondayIsoOf(), PreviewProfile())
                _uiState.value = PreviewUiState.Success(bundle)
            } catch (e: Exception) {
                _uiState.value = PreviewUiState.Error(e.message ?: "Could not generate preview")
            }
        }
    }

    fun queueForImport() {
        val bundle = (uiState.value as? PreviewUiState.Success)?.bundle ?: return
        importQueue.enqueue(bundle)
    }
}

sealed interface PreviewUiState {
    data object Idle : PreviewUiState
    data object Loading : PreviewUiState
    data class Success(val bundle: PreviewBundle) : PreviewUiState
    data class Error(val message: String) : PreviewUiState
}
