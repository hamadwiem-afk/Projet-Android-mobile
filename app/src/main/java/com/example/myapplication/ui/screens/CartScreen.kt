package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.myapplication.data.model.CartItem
import com.example.myapplication.ui.components.CoffeeBottomBar
import com.example.myapplication.ui.components.QuantityStepper
import com.example.myapplication.ui.components.StatusBarIcons
import com.example.myapplication.ui.theme.CoffeeColors
import com.example.myapplication.ui.viewmodel.CoffeeViewModel

@Composable
fun CartScreen(
    onCoffeeClick: (String, String) -> Unit,
    onCheckout: () -> Unit,
    onNavigate: (String) -> Unit,
    viewModel: CoffeeViewModel = viewModel()
) {

    StatusBarIcons(darkIcons = false)

    val cart by viewModel.cart.collectAsState()

    val totalItems = cart.sumOf { it.quantity }

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
                .widthIn(max = 760.dp)
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 18.dp)
        ) {

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Mon panier",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = CoffeeColors.OnDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "$totalItems article(s)",
                fontSize = 14.sp,
                color = CoffeeColors.Muted
            )

            Spacer(modifier = Modifier.height(18.dp))

            if (cart.isEmpty()) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(text = "🛒", fontSize = 48.sp)

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Votre panier est vide.\nAjoutez un café avec le bouton +.",
                            color = CoffeeColors.Muted,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                    }
                }

            } else {

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(
                        items = cart,
                        key = { it.coffee.key }
                    ) { item ->

                        CartRow(
                            item = item,

                            onClick = {
                                onCoffeeClick(
                                    item.coffee.type.path,
                                    item.coffee.id
                                )
                            },

                            onMinus = {
                                viewModel.changeQuantity(
                                    item.coffee.key,
                                    -1
                                )
                            },

                            onPlus = {
                                viewModel.changeQuantity(
                                    item.coffee.key,
                                    1
                                )
                            },

                            onRemove = {
                                viewModel.removeFromCart(
                                    item.coffee.key
                                )
                            }
                        )
                    }
                }

                // ---------- RÉSUMÉ + VALIDATION ----------

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp, top = 4.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "Total articles",
                            color = CoffeeColors.Muted,
                            fontSize = 15.sp,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = "$totalItems",
                            color = CoffeeColors.OnDark,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onCheckout,

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),

                        shape = CircleShape,

                        colors = ButtonDefaults.buttonColors(
                            containerColor = CoffeeColors.Accent
                        )
                    ) {

                        Text(
                            text = "Passer la commande",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        CoffeeBottomBar(
            selected = "cart",
            cartCount = totalItems,
            onNavigate = onNavigate
        )
    }
}


/* =========================================================
   LIGNE DU PANIER
   ========================================================= */

@Composable
private fun CartRow(
    item: CartItem,
    onClick: () -> Unit,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    onRemove: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(CoffeeColors.Surface)
            .clickable { onClick() }
            .padding(12.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            model = item.coffee.image,

            contentDescription = item.coffee.title,

            modifier = Modifier
                .size(76.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(CoffeeColors.SurfaceHigh),

            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {

            Text(
                text = item.coffee.title,
                color = CoffeeColors.OnDark,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = item.coffee.type.label,
                color = CoffeeColors.Muted,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            QuantityStepper(
                quantity = item.quantity,
                onMinus = onMinus,
                onPlus = onPlus
            )
        }

        IconButton(onClick = onRemove) {

            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Retirer du panier",
                tint = CoffeeColors.Muted
            )
        }
    }
}
