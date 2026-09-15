package de.ziven.android.ui.list

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.ziven.android.R
import de.ziven.shared.model.ShopItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(viewModel: ListViewModel) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_list)) },
                actions = {
                    IconButton(onClick = viewModel::generate) {
                        Icon(Icons.Default.Add, contentDescription = "Generate")
                    }
                    IconButton(onClick = {
                        val text = viewModel.shareText()
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, text)
                        }
                        context.startActivity(Intent.createChooser(intent, "Liste teilen"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Teilen")
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
                is ListUiState.Loading -> CircularProgressIndicator()
                is ListUiState.Error -> {
                    Text(s.message, color = MaterialTheme.colorScheme.error)
                    IconButton(onClick = viewModel::load) {
                        Icon(Icons.Default.Refresh, contentDescription = "Retry")
                    }
                }
                is ListUiState.Success -> {
                    val grouped = s.list.items.groupBy { it.category }
                    if (grouped.isEmpty()) {
                        Text("Die Liste ist leer.")
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            grouped.toSortedMap().forEach { (category, items) ->
                                item(key = category) {
                                    Text(
                                        category.uppercase(),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                }
                                items(items.sortedBy { it.checked }, key = { it.id }) { item ->
                                    ShopItemRow(item = item, onToggle = { viewModel.toggle(item) })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShopItemRow(item: ShopItem, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = item.checked, onCheckedChange = { onToggle() })
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(
                text = item.name.de,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = "${formatQty(item.quantity)} ${item.unit}",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

private fun formatQty(q: Double): String =
    if (q == q.toLong().toDouble()) q.toLong().toString() else q.toString()
