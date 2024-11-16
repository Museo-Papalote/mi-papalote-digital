package com.example.mipapalotedigital.utils
import androidx.compose.ui.graphics.Color
import com.example.mipapalotedigital.R
import com.example.mipapalotedigital.models.Zona

object ZonaManager {
    val zonas = listOf(
        Zona(
            id = "0zGFqkcIl1vo77p1rDhl",
            nombre = "Pequeños",
            color = "#009CA6"
        ),
        Zona(
            id = "9hkzu2aJxSZDybUzsdAb",
            nombre = "Comunico",
            color = "#0070BA"
        ),
        Zona(
            id = "CmQZ4MLp6M7c26s7h9Xg",
            nombre = "Pertenezco",
            color = "#87B734"
        ),
        Zona(
            id = "LHPJtOwRUqojQwn6yeJ3",
            nombre = "Comprendo",
            color = "#92278F"
        ),
        Zona(
            id = "RI0rBOL5odQ7EmPVtvSz",
            nombre = "Soy",
            color = "#E31837"
        ),
        Zona(
            id = "mOMM1tyb7izKgyU4D1kP",
            nombre = "Expreso",
            color = "#FF8200"
        )
    )

    // Sobrecarga para aceptar tanto String como Zona
    fun getLogoResId(zona: Zona): Int {
        return when (zona.color.uppercase()) {
            "#009CA6" -> R.drawable.pequenos
            "#0070BA" -> R.drawable.comunico
            "#87B734" -> R.drawable.pertenezco
            "#92278F" -> R.drawable.comprendo
            "#E31837" -> R.drawable.soy
            "#FF8200" -> R.drawable.expreso
            else -> R.drawable.logo_papalote
        }
    }

    fun getLogoResId(zonaId: String): Int {
        val zona = getZonaById(zonaId)
        return zona?.let { getLogoResId(it) } ?: R.drawable.logo_papalote
    }

    fun getZonaById(id: String): Zona? = zonas.find { it.id == id }

    fun getZonaByNombre(nombre: String): Zona? = zonas.find { it.nombre == nombre }

    fun getZonaByColor(color: String): Zona? = zonas.find {
        it.color.uppercase() == color.uppercase()
    }

    // Sobrecarga para aceptar tanto String como Zona
    fun getColorForZona(zona: Zona): Color {
        return Color(android.graphics.Color.parseColor(zona.color))
    }

    fun getColorForZona(zonaId: String): Color {
        return Color(android.graphics.Color.parseColor(
            getZonaById(zonaId)?.color ?: "#000000"
        ))
    }

    // Función de ayuda para debug
    fun logZonaInfo(zona: Zona) {
        println("Zona ID: ${zona.id}")
        println("Zona nombre: ${zona.nombre}")
        println("Color: ${zona.color}")
    }

    fun logZonaInfo(zonaId: String) {
        val zona = getZonaById(zonaId)
        println("Zona ID: $zonaId")
        println("Zona encontrada: ${zona?.nombre}")
        println("Color: ${zona?.color}")
    }
}