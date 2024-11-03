package com.example.mipapalotedigital.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val title: String
) {
    object Home : BottomNavItem(
        route = NavRoutes.HOME,
        icon = Icons.Filled.Home,
        title = "Inicio"
    )
    object Achievements : BottomNavItem(
        route = NavRoutes.ACHIEVEMENTS,
        icon = Icons.Filled.EmojiEvents,
        title = "Logros"
    )
    object Camera : BottomNavItem(
        route = NavRoutes.CAMERA,
        icon = Icons.Filled.PhotoCamera,
        title = "Cámara"
    )
    object Map : BottomNavItem(
        route = NavRoutes.MAP,
        icon = Icons.Filled.Place,
        title = "Mapa"
    )
    object Album : BottomNavItem(
        route = NavRoutes.ALBUM,
        icon = Icons.Filled.PhotoLibrary,
        title = "Álbum"
    )
}
