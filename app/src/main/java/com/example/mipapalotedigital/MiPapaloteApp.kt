package com.example.mipapalotedigital

import android.app.Application
import com.google.firebase.FirebaseApp

class MiPapaloteApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}