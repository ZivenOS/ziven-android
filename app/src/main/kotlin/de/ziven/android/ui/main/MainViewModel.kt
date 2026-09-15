package de.ziven.android.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.ziven.android.data.plan.PlanRepository
import de.ziven.android.data.preview.ImportQueue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val importQueue: ImportQueue,
    private val planRepository: PlanRepository,
) : ViewModel() {

    private val _importResult = MutableStateFlow<String?>(null)
    val importResult: StateFlow<String?> = _importResult.asStateFlow()

    fun consumePendingImport() {
        val bundle = importQueue.consume() ?: return
        viewModelScope.launch {
            try {
                planRepository.importPreview(bundle)
                _importResult.value = "Vorschau wurde importiert"
            } catch (e: Exception) {
                _importResult.value = e.message ?: "Import fehlgeschlagen"
            }
        }
    }

    fun clearImportResult() { _importResult.value = null }
}
