package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.CoffeeColors

/**
 * Barre du bas : Accueil / Favoris / Panier.
 * selected = "coffees", "favorites" ou "cart"
 */
@Composable
fun CoffeeBottomBar(
    selected: String,
    cartCount: Int,
    onNavigate: (String) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                CoffeeColors.Surface,
                RoundedCornerShape(
                    topStart = 28.dp,
                    topEnd = 28.dp
                )
            )
            .navigationBarsPadding()
            .padding(vertical = 12.dp),

        horizontalArrangement = Arrangement.SpaceAround
    ) {

        BarItem(
            label = "Accueil",
            icon = Icons.Default.Home,
            selected = selected == "coffees",
            badge = 0,
            onClick = { onNavigate("coffees") }
        )

        BarItem(
            label = "Favoris",
            icon = Icons.Default.Favorite,
            selected = selected == "favorites",
            badge = 0,
            onClick = { onNavigate("favorites") }
        )

        BarItem(
            label = "Panier",
            icon = Icons.Default.ShoppingCart,
            selected = selected == "cart",
            badge = cartCount,
            onClick = { onNavigate("cart") }
        )
    }
}

@Composable
private fun BarItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    badge: Int,
    onClick: () -> Unit
) {

    val color =
        if (selected) CoffeeColors.Accent else CoffeeColors.Muted

    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 2.dp),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        BadgedBox(
            badge = {
                if (badge > 0) {
                    Badge(containerColor = CoffeeColors.Accent) {
                        Text(
                            text = "$badge",
                            color = CoffeeColors.OnDark
                        )
                    }
                }
            }
        ) {

            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = label,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
