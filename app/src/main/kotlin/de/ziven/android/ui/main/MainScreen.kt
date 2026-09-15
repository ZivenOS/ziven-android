package de.ziven.android.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import de.ziven.android.R
import de.ziven.android.ui.list.ListScreen
import de.ziven.android.ui.list.ListViewModel
import de.ziven.android.ui.more.MoreScreen
import de.ziven.android.ui.more.MoreViewModel
import de.ziven.android.ui.pantry.PantryScreen
import de.ziven.android.ui.pantry.PantryViewModel
import de.ziven.android.ui.plan.PlanScreen
import de.ziven.android.ui.plan.PlanViewModel
import de.ziven.android.ui.theme.ZivenBackdrop
import de.ziven.android.ui.today.TodayScreen
import de.ziven.android.ui.today.TodayViewModel

@Composable
fun MainScreen(onLogout: () -> Unit, onCookSlot: (String) -> Unit, onScanBarcode: () -> Unit) {
    var selected by remember { mutableIntStateOf(0) }
    val snackbarHost = remember { SnackbarHostState() }
    val mainViewModel: MainViewModel = hiltViewModel()
    val importResult by mainViewModel.importResult.collectAsState()

    LaunchedEffect(Unit) { mainViewModel.consumePendingImport() }

    LaunchedEffect(importResult) {
        importResult?.let {
            snackbarHost.showSnackbar(it)
            mainViewModel.clearImportResult()
        }
    }

    val items = listOf(
        stringResource(R.string.nav_today) to Icons.Default.Home,
        stringResource(R.string.nav_plan) to Icons.Default.Add,
        stringResource(R.string.nav_list) to Icons.AutoMirrored.Filled.List,
        stringResource(R.string.nav_pantry) to Icons.Default.ShoppingCart,
        stringResource(R.string.nav_more) to Icons.Default.MoreVert,
    )

    Box(modifier = Modifier.fillMaxSize()) {
        ZivenBackdrop(modifier = Modifier.fillMaxSize())
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHost) },
            bottomBar = {
                NavigationBar {
                    items.forEachIndexed { index, (label, icon) ->
                        NavigationBarItem(
                            selected = selected == index,
                            onClick = { selected = index },
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label) },
                        )
                    }
                }
            },
        ) { padding ->
        val modifier = Modifier.padding(padding)
        when (selected) {
            0 -> TodayScreen(onCookSlot = onCookSlot, viewModel = hiltViewModel())
            1 -> PlanScreen(onCookSlot = onCookSlot, viewModel = hiltViewModel())
            2 -> ListScreen(viewModel = hiltViewModel())
            3 -> PantryScreen(onScanBarcode = onScanBarcode, viewModel = hiltViewModel())
            4 -> MoreScreen(onLogout = onLogout, viewModel = hiltViewModel())
        }
    }
    }
}
