package de.ziven.android.ui.pantry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import de.ziven.android.R
import de.ziven.shared.model.PantryItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantryScreen(onScanBarcode: () -> Unit, viewModel: PantryViewModel) {
    val state by viewModel.uiState.collectAsState()
    var showAdd by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.nav_pantry)) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) {
                Icon(Icons.Default.Add, contentDescription = "Hinzufügen")
            }
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
                is PantryUiState.Loading -> CircularProgressIndicator()
                is PantryUiState.Error -> Text(s.message, color = MaterialTheme.colorScheme.error)
                is PantryUiState.Success -> {
                    if (s.items.isEmpty()) {
                        Text("Der Vorrat ist leer.")
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(s.items, key = { it.id }) { item ->
                                PantryItemCard(item = item, onDiscard = { viewModel.discard(item) }, onDelete = { viewModel.delete(item) })
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAdd) {
        AddProductDialog(
            onDismiss = { showAdd = false },
            onScan = {
                showAdd = false
                onScanBarcode()
            },
            onAdd = { barcode, name, brand, quantity, unit ->
                viewModel.addProduct(barcode, name, brand, quantity, unit)
                showAdd = false
            },
        )
    }
}

@Composable
fun PantryItemCard(item: PantryItem, onDiscard: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(item.name.de, style = MaterialTheme.typography.titleMedium)
            Text("${item.quantity} ${item.unit} · ${item.location}", style = MaterialTheme.typography.bodyMedium)
            item.daysToExpiry?.let {
                val color = if (it < 3) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                Text("MHD in $it Tagen", color = color, style = MaterialTheme.typography.bodySmall)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onDiscard) { Text("Entsorgen") }
                TextButton(onClick = onDelete) { Text("Löschen") }
            }
        }
    }
}

@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onScan: () -> Unit,
    onAdd: (barcode: String, name: String, brand: String?, quantity: Double, unit: String) -> Unit,
) {
    var barcode by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var unit by remember { mutableStateOf("g") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Produkt hinzufügen") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = barcode, onValueChange = { barcode = it }, label = { Text("Barcode") }, singleLine = true)
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, singleLine = true)
                OutlinedTextField(value = brand, onValueChange = { brand = it }, label = { Text("Marke") }, singleLine = true)
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Menge") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )
                OutlinedTextField(value = unit, onValueChange = { unit = it }, label = { Text("Einheit") }, singleLine = true)
                TextButton(onClick = onScan) { Text("Barcode scannen") }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val qty = quantity.toDoubleOrNull() ?: 1.0
                    onAdd(barcode, name, brand.takeIf { it.isNotBlank() }, qty, unit)
                },
                enabled = barcode.isNotBlank() && name.isNotBlank() && quantity.isNotBlank() && unit.isNotBlank(),
            ) { Text("Hinzufügen") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Abbrechen") } },
    )
}
