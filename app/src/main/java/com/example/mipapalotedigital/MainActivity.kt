package com.example.mipapalotedigital

import BottomNavigationBar
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mipapalotedigital.navigation.NavRoutes
import com.example.mipapalotedigital.ui.screens.HomeScreen
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

    Scaffold(
        bottomBar = {
            if (usuarioViewModel.isUserLoggedIn()) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.LOGIN,
            modifier = Modifier.padding(paddingValues)
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
                HomeScreen(usuarioViewModel = usuarioViewModel)
            }
            composable(NavRoutes.ACHIEVEMENTS) {
                Text(text = "Logros Screen")
            }
            composable(NavRoutes.CAMERA) {
                Text(text = "Cámara Screen")
            }
            composable(NavRoutes.MAP) {
                Text(text = "Mapa Screen")
            }
            composable(NavRoutes.ALBUM) {
                Text(text = "Álbum Screen")
            }
        }
    }
}