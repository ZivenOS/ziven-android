package de.ziven.android.ui.plan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.ziven.android.R
import de.ziven.shared.model.PlanSlot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanScreen(
    onCookSlot: (String) -> Unit,
    viewModel: PlanViewModel,
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_plan)) },
                actions = {
                    IconButton(onClick = viewModel::generate) {
                        Icon(Icons.Default.Add, contentDescription = "Generate")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            when (val s = state) {
                is PlanUiState.Loading -> CircularProgressIndicator()
                is PlanUiState.Error -> Text(s.message, color = MaterialTheme.colorScheme.error)
                is PlanUiState.Success -> {
                    Text(
                        text = "KW ${s.weekStart}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        s.days.forEach { (day, slots) ->
                            item(key = day) {
                                Text(
                                    text = dayName(day),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.secondary,
                                )
                            }
                            items(slots.sortedBy { slotOrder(it.slot) }, key = { it.id }) { slot ->
                                PlanSlotCard(slot = slot, onCook = { onCookSlot(slot.id) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlanSlotCard(slot: PlanSlot, onCook: () -> Unit) {
    val title = slot.recipeTitle?.de ?: slot.label?.de ?: slotKindLabel(slot.kind)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(slot.slot.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelLarge)
            Text(title, style = MaterialTheme.typography.titleMedium)
        }
    }
}

private fun slotOrder(slot: String): Int = when (slot) {
    "breakfast" -> 0
    "lunch" -> 1
    "dinner" -> 2
    "snack" -> 3
    else -> 4
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

private fun slotKindLabel(kind: String): String = when (kind) {
    "recipe" -> "Rezept"
    "leftover" -> "Reste"
    "eating_out" -> "Essen gehen"
    "skip" -> "Überspringen"
    else -> kind
}
