package de.ziven.android.ui.more

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import de.ziven.android.R
import de.ziven.android.ui.common.mondayIsoOf
import de.ziven.shared.model.HouseholdMember
import de.ziven.shared.model.ProfilePatch

enum class MorePage { Hub, Nutrition, Household, Profile, Weight, Legal, Export, Delete }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(onLogout: () -> Unit, viewModel: MoreViewModel) {
    var page by remember { mutableStateOf(MorePage.Hub) }
    val snackbarHost = remember { SnackbarHostState() }
    val event by viewModel.eventState.collectAsState()

    LaunchedEffect(event) {
        event?.let {
            snackbarHost.showSnackbar((it as MoreEvent.Message).text)
            viewModel.clearEvent()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nav_more)) },
                navigationIcon = {
                    if (page != MorePage.Hub) {
                        IconButton(onClick = { page = MorePage.Hub }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                        }
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHost) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
        ) {
            when (page) {
                MorePage.Hub -> MoreHub(onPage = { page = it }, onLogout = onLogout)
                MorePage.Nutrition -> NutritionPage(viewModel = viewModel)
                MorePage.Household -> HouseholdPage(viewModel = viewModel)
                MorePage.Profile -> ProfilePage(viewModel = viewModel)
                MorePage.Weight -> WeightPage(viewModel = viewModel)
                MorePage.Legal -> LegalPage()
                MorePage.Export -> ExportPage(viewModel = viewModel)
                MorePage.Delete -> DeletePage(viewModel = viewModel, onDeleted = onLogout)
            }
        }
    }
}

@Composable
fun MoreHub(onPage: (MorePage) -> Unit, onLogout: () -> Unit) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { HubCard("Ernährung", Icons.Default.Person) { onPage(MorePage.Nutrition) } }
        item { HubCard("Haushalt", Icons.Default.AccountCircle) { onPage(MorePage.Household) } }
        item { HubCard("Profil", Icons.Default.Person) { onPage(MorePage.Profile) } }
        item { HubCard("Gewicht", Icons.Default.Person) { onPage(MorePage.Weight) } }
        item { HubCard("Rechtliches", Icons.Default.Info) { onPage(MorePage.Legal) } }
        item { HubCard("Daten exportieren", Icons.Default.Share) { onPage(MorePage.Export) } }
        item { HubCard("Account löschen", Icons.Default.Delete) { onPage(MorePage.Delete) } }
        item { HubCard("Abmelden", Icons.AutoMirrored.Filled.ExitToApp) { onLogout() } }
    }
}

@Composable
fun HubCard(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        ListItem(
            headlineContent = { Text(label) },
            leadingContent = { Icon(icon, contentDescription = null) },
        )
    }
}

@Composable
fun NutritionPage(viewModel: MoreViewModel) {
    val state by viewModel.nutritionState.collectAsState()
    LaunchedEffect(Unit) { viewModel.loadNutrition() }

    when (val s = state) {
        is NutritionUiState.Loading -> CircularProgressIndicator()
        is NutritionUiState.Error -> Text(s.message, color = MaterialTheme.colorScheme.error)
        is NutritionUiState.Success -> {
            var useKj by remember { mutableStateOf(s.useKj) }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Woche ${s.week.weekStart}", style = MaterialTheme.typography.titleLarge)
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Text("kJ anzeigen", modifier = Modifier.weight(1f))
                    Button(onClick = { useKj = !useKj }) { Text(if (useKj) "kcal" else "kJ") }
                }
                s.week.week.target?.let { target ->
                    MacroRow("Ziel", target, useKj)
                }
                MacroRow("Ist", s.week.week.actual, useKj)
                HorizontalDivider()
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(s.week.days) { day ->
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(day.date)
                                MacroRow("Ist", day.actual, useKj)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MacroRow(label: String, macros: de.ziven.shared.model.Macros, useKj: Boolean) {
    val kcal = if (useKj) macros.kcal * 4.184 else macros.kcal
    val unit = if (useKj) "kJ" else "kcal"
    Text("$label: ${kcal.toInt()} $unit · ${macros.protein.toInt()}g P · ${macros.carbs.toInt()}g K · ${macros.fat.toInt()}g F")
}

@Composable
fun HouseholdPage(viewModel: MoreViewModel) {
    val state by viewModel.householdState.collectAsState()
    LaunchedEffect(Unit) { viewModel.loadHousehold() }

    var showInvite by remember { mutableStateOf(false) }
    when (val s = state) {
        is HouseholdUiState.Loading -> CircularProgressIndicator()
        is HouseholdUiState.Error -> Text(s.message, color = MaterialTheme.colorScheme.error)
        is HouseholdUiState.Success -> {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(s.household.name, style = MaterialTheme.typography.titleLarge)
                Text("Lieblingsgeschäft: ${s.household.preferredStore ?: "—"}")
                Text("Mitglieder", style = MaterialTheme.typography.titleMedium)
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(s.household.members, key = { it.id }) { member ->
                        MemberCard(member = member)
                    }
                }
                Button(onClick = { showInvite = true }) { Text("Mitglied einladen") }
            }
            if (showInvite) {
                var email by remember { mutableStateOf("") }
                var role by remember { mutableStateOf("member") }
                AlertDialog(
                    onDismissRequest = { showInvite = false },
                    title = { Text("Einladen") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("E-Mail") })
                            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                Checkbox(checked = role == "admin", onCheckedChange = { role = if (it) "admin" else "member" })
                                Text("Admin-Rechte")
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = { viewModel.invite(email, role); showInvite = false },
                            enabled = email.isNotBlank(),
                        ) { Text("Einladen") }
                    },
                    dismissButton = { TextButton(onClick = { showInvite = false }) { Text("Abbrechen") } },
                )
            }
        }
    }
}

@Composable
fun MemberCard(member: HouseholdMember) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(member.displayName ?: member.email, style = MaterialTheme.typography.titleMedium)
            Text(member.role)
            if (member.dietary.isNotEmpty()) Text("Ernährung: ${member.dietary.joinToString(",")}")
        }
    }
}

@Composable
fun ProfilePage(viewModel: MoreViewModel) {
    val state by viewModel.householdState.collectAsState()
    LaunchedEffect(Unit) { viewModel.loadHousehold() }

    val member = (state as? HouseholdUiState.Success)?.household?.members?.firstOrNull()
    if (member == null) {
        CircularProgressIndicator()
        return
    }

    var displayName by remember(member.id) { mutableStateOf(member.displayName ?: "") }
    var kcalTarget by remember(member.id) { mutableStateOf(member.kcalTarget?.toString() ?: "") }
    var weightKg by remember(member.id) { mutableStateOf(member.weightKg?.toString() ?: "") }
    val dietaryOptions = listOf("vegetarian", "vegan", "pescetarian", "gluten_free", "lactose_free", "nut_free", "shellfish_free", "halal", "kosher")
    val selected = remember(member.id) { member.dietary.toMutableSet() }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Profil", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(value = displayName, onValueChange = { displayName = it }, label = { Text("Anzeigename") }, modifier = Modifier.fillMaxWidth())
        Text("Ernährungsformen", style = MaterialTheme.typography.titleMedium)
        dietaryOptions.forEach { option ->
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Checkbox(checked = option in selected, onCheckedChange = { checked ->
                    if (checked) selected.add(option) else selected.remove(option)
                })
                Text(option)
            }
        }
        OutlinedTextField(value = kcalTarget, onValueChange = { kcalTarget = it }, label = { Text("Kcal-Ziel") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = weightKg, onValueChange = { weightKg = it }, label = { Text("Gewicht (kg)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
        Button(onClick = {
            viewModel.updateProfile(
                member.id,
                ProfilePatch(
                    displayName = displayName.takeIf { it.isNotBlank() },
                    dietary = selected.toList(),
                    kcalTarget = kcalTarget.toIntOrNull(),
                    weightKg = weightKg.toDoubleOrNull(),
                ),
            )
        }) { Text("Speichern") }
    }
}

@Composable
fun WeightPage(viewModel: MoreViewModel) {
    val state by viewModel.weightState.collectAsState()
    val household by viewModel.householdState.collectAsState()
    val member = (household as? HouseholdUiState.Success)?.household?.members?.firstOrNull()
    LaunchedEffect(member) { member?.let { viewModel.loadWeight(it.id) } }

    var kg by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Gewicht", style = MaterialTheme.typography.titleLarge)
        when (val s = state) {
            is WeightUiState.Loading -> CircularProgressIndicator()
            is WeightUiState.Error -> Text(s.message, color = MaterialTheme.colorScheme.error)
            is WeightUiState.Success -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(s.entries) { entry ->
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(entry.loggedOn)
                                Text("${entry.kg} kg", style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                }
            }
        }
        OutlinedTextField(value = kg, onValueChange = { kg = it }, label = { Text("Heute (kg)" )}, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
        Button(
            onClick = {
                member?.let { viewModel.logWeight(it.id, kg.toDoubleOrNull() ?: 0.0) }
                kg = ""
            },
            enabled = kg.isNotBlank() && member != null,
        ) { Text("Eintragen") }
    }
}

@Composable
fun LegalPage() {
    val scroll = rememberScrollState()
    Column(modifier = Modifier.verticalScroll(scroll), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Impressum", style = MaterialTheme.typography.titleLarge)
        Text("Michael Adam\nDeutschland\nKontakt: bitte im Einstellungsmenü der Web-App hinterlegen")
        HorizontalDivider()
        Text("Datenschutz", style = MaterialTheme.typography.titleLarge)
        Text("Ziven verarbeitet personenbezogene Daten nur zum Betrieb des Haushaltsplaners.\n\nRechtsgrundlage: Art. 6 DSGVO (Vertrag/ berechtigtes Interesse).\n\nHosting, Zahlung und E-Mail-Provider werden in der Web-App und in den AGB aufgeführt.\n\nDu kannst deine Daten jederzeit über „Daten exportieren“ herunterladen und deinen Account löschen.")
        Text("Kontaktdaten des Verantwortlichen folgen vor Livegang.", color = MaterialTheme.colorScheme.error)
    }
}

@Composable
fun ExportPage(viewModel: MoreViewModel) {
    val context = LocalContext.current
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Datenexport", style = MaterialTheme.typography.titleLarge)
        Text("Lade eine maschinenlesbare Kopie deiner Ziven-Daten herunter.")
        Button(onClick = {
            viewModel.exportAccount { json ->
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/json"
                    putExtra(Intent.EXTRA_TEXT, json)
                }
                context.startActivity(Intent.createChooser(intent, "Export teilen"))
            }
        }) { Text("Export erstellen & teilen") }
    }
}

@Composable
fun DeletePage(viewModel: MoreViewModel, onDeleted: () -> Unit) {
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Account löschen", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.error)
        Text("Dies löscht deinen Account und Haushaltsdaten dauerhaft. Bitte gib dein Passwort ein.")
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Passwort") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
        )
        Button(
            onClick = { confirm = true },
            enabled = password.isNotBlank(),
        ) { Text("Account löschen", color = MaterialTheme.colorScheme.onError) }
    }
    if (confirm) {
        AlertDialog(
            onDismissRequest = { confirm = false },
            title = { Text("Wirklich löschen?") },
            text = { Text("Alle Daten werden entfernt. Diese Aktion ist unwiderruflich.") },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.deleteAccount(password) { onDeleted() } },
                ) { Text("Löschen") }
            },
            dismissButton = { TextButton(onClick = { confirm = false }) { Text("Abbrechen") } },
        )
    }
}
