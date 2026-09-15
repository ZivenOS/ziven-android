package de.ziven.android.ui.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.ziven.shared.model.PreviewBundle
import de.ziven.shared.model.PreviewRecipe
import de.ziven.shared.model.PreviewSlot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewScreen(
    onRegisterForImport: () -> Unit,
    viewModel: PreviewViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Deine Woche") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            when (val s = state) {
                is PreviewUiState.Idle -> {
                    Text("Sieh dir eine Beispiel-Woche an – ohne Account.")
                    Button(onClick = viewModel::generatePreview) { Text("Woche generieren") }
                    TextButton(onClick = onRegisterForImport) { Text("Ich habe bereits einen Account") }
                }
                is PreviewUiState.Loading -> CircularProgressIndicator()
                is PreviewUiState.Error -> Text(s.message, color = MaterialTheme.colorScheme.error)
                is PreviewUiState.Success -> PreviewResult(
                    bundle = s.bundle,
                    onRegisterForImport = {
                        viewModel.queueForImport()
                        onRegisterForImport()
                    },
                )
            }
        }
    }
}

@Composable
fun PreviewResult(bundle: PreviewBundle, onRegisterForImport: () -> Unit) {
    val recipesById = bundle.recipes.associateBy { it.id }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Vorschau KW ${bundle.weekStart}", style = MaterialTheme.typography.titleLarge)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            val grouped = bundle.placements.groupBy { it.day }.toSortedMap()
            grouped.forEach { (day, slots) ->
                item(key = day) {
                    Text(dayName(day), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)
                }
                items(slots.size, key = { "$day:$it" }) { index ->
                    val slot = slots[index]
                    val recipe = recipesById[slot.recipeId]
                    DaySlotCard(slot = slot, recipe = recipe)
                }
            }
        }
        Button(onClick = onRegisterForImport) { Text("Mit Account speichern") }
    }
}

@Composable
fun DaySlotCard(slot: PreviewSlot, recipe: PreviewRecipe?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(slot.slot.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelLarge)
            Text(recipe?.title?.de ?: "Unbekanntes Rezept", style = MaterialTheme.typography.titleMedium)
            recipe?.let { r ->
                Text("${r.totalMinutes} min · ${r.cuisine}", style = MaterialTheme.typography.bodySmall)
                Text("Halal: ${r.confidence.halal}, Koscher: ${r.confidence.kosher}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

private fun dayName(day: Int): String = when (day) {
    0 -> "Montag"
    1 -> "Dienstag"
    2 -> "Mittwoch"
    3 -> "Donnerstag"
    4 -> "Freitag"
    5 -> "Samstag"
    6 -> "Sonntag"
    else -> "Tag $day"
}
