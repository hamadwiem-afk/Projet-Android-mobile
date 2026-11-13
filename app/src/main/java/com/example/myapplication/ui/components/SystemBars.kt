package com.example.myapplication.ui.components

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Choisit la couleur des icônes de la barre d'état :
 * darkIcons = true  -> icônes sombres (écran clair, ex : accueil beige)
 * darkIcons = false -> icônes claires (écrans sombres)
 */
@Composable
fun StatusBarIcons(darkIcons: Boolean) {

    val view = LocalView.current

    if (!view.isInEditMode) {

        SideEffect {

            val window =
                (view.context as? Activity)?.window
                    ?: return@SideEffect

            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = darkIcons
                isAppearanceLightNavigationBars = darkIcons
            }
        }
    }
}
