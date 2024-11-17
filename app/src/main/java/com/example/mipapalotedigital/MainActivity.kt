package com.example.mipapalotedigital

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import BottomNavigationBar
import com.example.mipapalotedigital.navigation.NavRoutes
import ActividadRepositoryImpl
import com.example.mipapalotedigital.ui.screens.*
import com.example.mipapalotedigital.ui.theme.MiPapaloteDigitalTheme
import ActividadViewModel
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mipapalotedigital.viewmodels.UsuarioViewModel
import com.example.mipapalotedigital.utils.AuthDataStore
import com.example.mipapalotedigital.repository.UsuarioRepositoryImpl
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!hasRequiredPermissions()) {
            ActivityCompat.requestPermissions(this, CAMERAX_PERMISSIONS, 0)
        }

        setContent {
            MiPapaloteDigitalTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainApp(context = this)
                }
            }
        }
    }

    fun hasRequiredPermissions(): Boolean {
        return CAMERAX_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(applicationContext, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        private val CAMERAX_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    }
}

@Composable
fun MainApp(context: ComponentActivity) {
    val authDataStore = AuthDataStore(context)
    val repository = UsuarioRepositoryImpl(authDataStore)

    val usuarioViewModel: UsuarioViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return UsuarioViewModel(repository) as T
            }
        }
    )

    val actividadViewModel: ActividadViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ActividadViewModel(ActividadRepositoryImpl()) as T
            }
        }
    )

    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val loginState by usuarioViewModel.loginState.collectAsState()
    val initRoute = if (loginState) NavRoutes.HOME else NavRoutes.LOGIN

    Scaffold(
        bottomBar = {
            if (loginState) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = initRoute,
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
                HomeScreen(
                    usuarioViewModel = usuarioViewModel,
                    actividadViewModel = actividadViewModel,
                    navController = navController
                )
            }
            composable(NavRoutes.ACHIEVEMENTS) {
                ProgressScreen(usuarioViewModel = usuarioViewModel)
            }
            composable(NavRoutes.CAMERA) {
                CameraScreen()
            }
            composable(NavRoutes.MAP) {
                FloorSelectorApp()
            }
            composable(NavRoutes.ALBUM) {
                Text(text = "Álbum Screen")
            }
            composable(NavRoutes.PROFILE) {
                UserProfileScreen(
                    navController = navController,
                    viewModel = usuarioViewModel
                )
            }
            composable(
                route = NavRoutes.ACTIVITY_DETAIL,
                arguments = listOf(
                    navArgument("activityId") {
                        type = NavType.StringType
                        nullable = false
                    }
                )
            ) { backStackEntry ->
                val activityId = backStackEntry.arguments?.getString("activityId")
                Log.d("Navigation", "Received activity ID in navigation: $activityId")

                if (!activityId.isNullOrEmpty()) {
                    ActivityDetailScreen(
                        activityId = activityId,
                        actividadViewModel = actividadViewModel,
                        navController = navController
                    )
                } else {
                    Log.e("Navigation", "Invalid activity ID received")
                    navController.popBackStack()
                }
            }
        }
    }
}
