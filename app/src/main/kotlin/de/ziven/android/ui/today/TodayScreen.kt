package de.ziven.android.ui.today

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
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
import de.ziven.android.ui.common.isoDayName
import de.ziven.shared.model.PlanSlot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(
    onCookSlot: (String) -> Unit,
    viewModel: TodayViewModel,
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_today)) },
                actions = {
                    IconButton(onClick = viewModel::generateWeek) {
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
                is TodayUiState.Loading -> CircularProgressIndicator()
                is TodayUiState.Error -> Text(s.message, color = MaterialTheme.colorScheme.error)
                is TodayUiState.Success -> {
                    Text(
                        text = isoDayName(s.date),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    s.message?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                    if (s.slots.isEmpty()) {
                        Text("Noch keine Mahlzeiten geplant.")
                        Button(onClick = viewModel::generateWeek) {
                            Text("Woche generieren")
                        }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(s.slots, key = { it.id }) { slot ->
                                TodaySlotCard(slot = slot, onCook = { onCookSlot(slot.id) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TodaySlotCard(slot: PlanSlot, onCook: () -> Unit) {
    val title = slot.recipeTitle?.de ?: slot.label?.de ?: slotKindLabel(slot.kind)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(slot.slot.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelLarge)
            Text(title, style = MaterialTheme.typography.titleMedium)
            if (slot.kind == "recipe") {
                Button(onClick = onCook, modifier = Modifier.padding(top = 8.dp)) {
                    Text("Kochen")
                }
            }
        }
    }
}

private fun slotKindLabel(kind: String): String = when (kind) {
    "recipe" -> "Rezept"
    "leftover" -> "Reste"
    "eating_out" -> "Essen gehen"
    "skip" -> "Überspringen"
    else -> kind
}
