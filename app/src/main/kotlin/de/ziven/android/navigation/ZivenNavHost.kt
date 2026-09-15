package de.ziven.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import de.ziven.android.ui.auth.AuthViewModel
import de.ziven.android.ui.auth.LoginScreen
import de.ziven.android.ui.auth.RegisterScreen
import de.ziven.android.ui.main.MainScreen

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Main : Screen("main")
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
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegister = { navController.navigate(Screen.Login.route) { popUpTo(Screen.Register.route) { inclusive = true } } },
                onGoToLogin = { navController.popBackStack() },
            )
        }
        composable(Screen.Main.route) {
            MainScreen(onLogout = { navController.navigate(Screen.Login.route) { popUpTo(Screen.Main.route) { inclusive = true } } })
        }
    }
}
