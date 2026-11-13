package com.example.myapplication.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.components.CoffeeBottomBar
import com.example.myapplication.ui.components.CoffeeCard
import com.example.myapplication.ui.components.StatusBarIcons
import com.example.myapplication.ui.theme.CoffeeColors
import com.example.myapplication.ui.viewmodel.CoffeeViewModel

@Composable
fun FavoritesScreen(
    onCoffeeClick: (String, String) -> Unit,
    onNavigate: (String) -> Unit,
    viewModel: CoffeeViewModel = viewModel()
) {

    StatusBarIcons(darkIcons = false)

    val context = LocalContext.current

    val favorites by viewModel.favorites.collectAsState()
    val uniqueFavorites = favorites.distinctBy { it.key }
    val cart by viewModel.cart.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CoffeeColors.Background)
    ) {

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .statusBarsPadding()
                .widthIn(max = 960.dp)
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 18.dp)
        ) {

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Mes favoris",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = CoffeeColors.OnDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${uniqueFavorites.size} café(s) enregistré(s)",
                fontSize = 14.sp,
                color = CoffeeColors.Muted
            )

            Spacer(modifier = Modifier.height(18.dp))

            if (uniqueFavorites.isEmpty()) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(text = "🤍", fontSize = 48.sp)

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Aucun favori pour l'instant.\nAppuyez sur le cœur d'un café pour l'ajouter.",
                            color = CoffeeColors.Muted,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                    }
                }

            } else {

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 155.dp),

                    modifier = Modifier.fillMaxSize(),

                    contentPadding = PaddingValues(bottom = 24.dp),

                    horizontalArrangement = Arrangement.spacedBy(12.dp),

                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    items(
                        items = uniqueFavorites,
                        key = { it.key }
                    ) { coffee ->

                        CoffeeCard(
                            coffee = coffee,

                            isFavorite = true,

                            onClick = {
                                onCoffeeClick(
                                    coffee.type.path,
                                    coffee.id
                                )
                            },

                            // Cliquer sur le cœur le retire des favoris
                            onFavoriteClick = {
                                viewModel.toggleFavorite(coffee)
                            },

                            onAddToCart = {
                                viewModel.addToCart(coffee)

                                Toast.makeText(
                                    context,
                                    "${coffee.title} ajouté au panier",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }
            }
        }

        CoffeeBottomBar(
            selected = "favorites",
            cartCount = cart.sumOf { it.quantity },
            onNavigate = onNavigate
        )
    }
}
