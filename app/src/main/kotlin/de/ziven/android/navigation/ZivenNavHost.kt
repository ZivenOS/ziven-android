package de.ziven.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import de.ziven.android.ui.auth.AuthViewModel
import de.ziven.android.ui.auth.LoginScreen
import de.ziven.android.ui.auth.RegisterScreen
import de.ziven.android.ui.barcode.BarcodeScreen
import de.ziven.android.ui.cook.CookScreen
import de.ziven.android.ui.main.MainScreen
import de.ziven.android.ui.preview.PreviewScreen

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Preview : Screen("preview")
    data object Main : Screen("main")
    data object Cook : Screen("cook/{slotId}") {
        fun createRoute(slotId: String) = "cook/${slotId}"
    }
    data object Barcode : Screen("barcode")
}

@Composable
fun ZivenNavHost(navController: NavHostController) {
    val viewModel: AuthViewModel = hiltViewModel()
    val isLoggedIn = viewModel.isLoggedIn.collectAsState().value

    val start = if (isLoggedIn) Screen.Main.route else Screen.Login.route

    NavHost(navController = navController, startDestination = start) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLogin = { navController.navigate(Screen.Main.route) { popUpTo(Screen.Login.route) { inclusive = true } } },
                onGoToRegister = { navController.navigate(Screen.Register.route) },
                onPreview = { navController.navigate(Screen.Preview.route) },
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegister = { navController.navigate(Screen.Main.route) { popUpTo(Screen.Register.route) { inclusive = true } } },
                onGoToLogin = { navController.popBackStack() },
            )
        }
        composable(Screen.Preview.route) {
            PreviewScreen(
                onRegisterForImport = { navController.navigate(Screen.Register.route) { popUpTo(Screen.Preview.route) { inclusive = true } } },
            )
        }
        composable(Screen.Main.route) {
            MainScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) { popUpTo(Screen.Main.route) { inclusive = true } }
                },
                onCookSlot = { slotId ->
                    navController.navigate(Screen.Cook.createRoute(slotId))
                },
                onScanBarcode = { navController.navigate(Screen.Barcode.route) },
            )
        }
        composable(
            route = Screen.Cook.route,
            arguments = listOf(navArgument("slotId") { type = NavType.StringType }),
        ) {
            CookScreen(
                onFinished = { navController.popBackStack() },
            )
        }
        composable(Screen.Barcode.route) {
            BarcodeScreen(onDone = { navController.popBackStack() })
        }
    }
}
