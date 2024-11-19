package com.example.mipapalotedigital.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val title: String
) {
    object Home : BottomNavItem(
        route = NavRoutes.HOME,
        icon = Icons.Outlined.Home,
        title = "Inicio"
    )
    object Achievements : BottomNavItem(
        route = NavRoutes.PROGRESS,
        icon = Icons.Outlined.Star,
        title = "Progreso"
    )
    object Camera : BottomNavItem(
        route = NavRoutes.CAMERA,
        icon = Icons.Outlined.CameraAlt,
        title = "Cámara"
    )
    object Map : BottomNavItem(
        route = NavRoutes.MAP,
        icon = Icons.Outlined.Explore,
        title = "Mapa"
    )
    object Album : BottomNavItem(
        route = NavRoutes.ALBUM,
        icon = Icons.Rounded.Collections,
        title = "Álbum"
    )
}
