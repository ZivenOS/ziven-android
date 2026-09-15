package de.ziven.android.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import de.ziven.android.R

@Composable
fun MainScreen(onLogout: () -> Unit) {
    var selected by remember { mutableIntStateOf(0) }
    val items = listOf(
        stringResource(R.string.nav_today) to Icons.Default.Home,
        stringResource(R.string.nav_plan) to Icons.Default.Add,
        stringResource(R.string.nav_list) to Icons.Default.List,
        stringResource(R.string.nav_pantry) to Icons.Default.ShoppingCart,
        stringResource(R.string.nav_more) to Icons.Default.MoreVert,
    )

    Scaffold(
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
            0 -> TodayPlaceholder(modifier)
            1 -> PlanPlaceholder(modifier)
            2 -> ListPlaceholder(modifier)
            3 -> PantryPlaceholder(modifier)
            4 -> MorePlaceholder(modifier)
        }
    }
}

@Composable
fun TodayPlaceholder(modifier: Modifier) = CenteredText("Heute", modifier)
@Composable
fun PlanPlaceholder(modifier: Modifier) = CenteredText("Plan", modifier)
@Composable
fun ListPlaceholder(modifier: Modifier) = CenteredText("Liste", modifier)
@Composable
fun PantryPlaceholder(modifier: Modifier) = CenteredText("Vorrat", modifier)

@Composable
fun MorePlaceholder(modifier: Modifier) = CenteredText("Mehr", modifier)
