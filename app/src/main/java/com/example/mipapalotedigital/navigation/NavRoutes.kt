package com.example.mipapalotedigital.navigation

object NavRoutes {
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val HOME = "home"
    const val ACHIEVEMENTS = "achievements"
    const val CAMERA = "camera"
    const val MAP = "map"
    const val ALBUM = "album"
    const val PROFILE = "profile"
    const val ACTIVITY_DETAIL = "activity/{activityId}"

    fun createActivityRoute(activityId: String): String {
        return "activity/$activityId"
    }
}