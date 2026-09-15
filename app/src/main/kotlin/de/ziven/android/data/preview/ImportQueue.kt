package de.ziven.android.data.preview

import de.ziven.shared.model.PreviewBundle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImportQueue @Inject constructor() {
    private val _pending = MutableStateFlow<PreviewBundle?>(null)
    val pending: StateFlow<PreviewBundle?> = _pending.asStateFlow()

    fun enqueue(bundle: PreviewBundle) { _pending.value = bundle }
    fun consume(): PreviewBundle? = _pending.value.also { _pending.value = null }
}
