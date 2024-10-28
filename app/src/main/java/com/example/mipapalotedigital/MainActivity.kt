package com.example.mipapalotedigital
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mipapalotedigital.navigation.NavRoutes
import com.example.mipapalotedigital.ui.screens.LoginScreen
import com.example.mipapalotedigital.ui.screens.SignUpScreen
import com.example.mipapalotedigital.ui.theme.MiPapaloteDigitalTheme
import com.example.mipapalotedigital.viewmodels.UsuarioViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MiPapaloteDigitalTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApp()
                }
            }
        }
    }
}

@Composable
fun MainApp() {
    val usuarioViewModel: UsuarioViewModel = viewModel()
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.LOGIN
    ) {
        composable(NavRoutes.LOGIN) {
            LoginScreen(
                usuarioViewModel = usuarioViewModel,
                onSignUpClick = { navController.navigate(NavRoutes.SIGNUP) },
                onLoginSuccess = {
                    coroutineScope.launch {
                        navController.navigate(NavRoutes.HOME) {
                            popUpTo(NavRoutes.LOGIN) { inclusive = true }
                        }
                    }
                }
            )
        }
        composable(NavRoutes.SIGNUP) {
            SignUpScreen(
                usuarioViewModel = usuarioViewModel,
                onLoginClick = { navController.navigate(NavRoutes.LOGIN) },
                onSignUpSuccess = {
                    coroutineScope.launch {
                        navController.navigate(NavRoutes.HOME) {
                            popUpTo(NavRoutes.SIGNUP) { inclusive = true }
                        }
                    }
                }
            )
        }
        composable(NavRoutes.HOME) {
            Text(text = "Home Screen")
        }
    }
}



