package de.ziven.android.ui.cook

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.ziven.shared.model.CookSession
import de.ziven.shared.model.CookStep

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CookScreen(
    onFinished: () -> Unit,
    viewModel: CookViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Kochen") }) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            when (val s = state) {
                is CookUiState.Loading -> CircularProgressIndicator()
                is CookUiState.Error -> Text(s.message, color = MaterialTheme.colorScheme.error)
                is CookUiState.Success -> CookSessionView(
                    session = s.session,
                    onComplete = viewModel::complete,
                )
                is CookUiState.Done -> {
                    Text("Gekocht & geloggt!", style = MaterialTheme.typography.headlineMedium)
                    Button(onClick = onFinished) {
                        Text("Fertig")
                    }
                }
            }
        }
    }
}

@Composable
fun CookSessionView(session: CookSession, onComplete: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        session.recipe?.let { recipe ->
            Text(recipe.title.de, style = MaterialTheme.typography.titleLarge)
            recipe.ovenSettings?.let { oven ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Ofen", style = MaterialTheme.typography.labelLarge)
                        Text("${oven.mode ?: ""} ${oven.temperature}°C, ${oven.durationMinutes} min")
                    }
                }
            }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            itemsIndexed(session.steps) { index, step ->
                StepCard(index = index, step = step)
            }
        }
        Button(onClick = onComplete, modifier = Modifier.fillMaxWidth()) {
            Text("Gekocht & geloggt")
        }
    }
}

@Composable
fun StepCard(index: Int, step: CookStep) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("${index + 1}", style = MaterialTheme.typography.labelLarge)
            Text(step.text.de, style = MaterialTheme.typography.bodyLarge)
            step.timer?.let { timer ->
                Text("Timer: ${timer.seconds / 60} min", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
