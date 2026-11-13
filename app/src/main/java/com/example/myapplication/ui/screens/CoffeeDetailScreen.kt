package com.example.myapplication.ui.screens

import android.widget.Toast
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.myapplication.data.model.Coffee
import com.example.myapplication.ui.components.QuantityStepper
import com.example.myapplication.ui.components.StatusBarIcons
import com.example.myapplication.ui.theme.CoffeeColors
import com.example.myapplication.ui.viewmodel.CoffeeViewModel

@Composable
fun CoffeeDetailScreen(
    coffeeType: String,
    coffeeId: String,
    viewModel: CoffeeViewModel = viewModel()
) {

    StatusBarIcons(darkIcons = false)

    val context = LocalContext.current

    val coffee by viewModel.selectedCoffee.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val favorites by viewModel.favorites.collectAsState()

    val backDispatcher =
        LocalOnBackPressedDispatcherOwner.current
            ?.onBackPressedDispatcher

    LaunchedEffect(coffeeType, coffeeId) {

        viewModel.loadCoffee(
            coffeeType,
            coffeeId
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CoffeeColors.Background)
    ) {

        when {

            // ---------- CHARGEMENT ----------

            isLoading -> {

                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = CoffeeColors.Accent
                )
            }

            // ---------- ERREUR ----------

            error != null -> {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),

                    horizontalAlignment = Alignment.CenterHorizontally,

                    verticalArrangement = Arrangement.Center
                ) {

                    Text(text = "☕", fontSize = 50.sp)

                    Spacer(modifier = Modifier.height(15.dp))

                    Text(
                        text = error ?: "Erreur",
                        color = CoffeeColors.OnDark,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            viewModel.loadCoffee(
                                coffeeType,
                                coffeeId
                            )
                        },

                        colors = ButtonDefaults.buttonColors(
                            containerColor = CoffeeColors.Accent
                        ),

                        shape = CircleShape
                    ) {

                        Text(
                            text = "Réessayer",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // ---------- CAFÉ CHARGÉ ----------

            coffee != null -> {

                val current = coffee!!

                CoffeeDetailContent(
                    coffee = current,

                    isFavorite =
                        favorites.any { it.key == current.key },

                    onBack = {
                        backDispatcher?.onBackPressed()
                    },

                    onToggleFavorite = {
                        viewModel.toggleFavorite(current)
                    },

                    onAddToCart = { quantity ->

                        viewModel.addToCart(current, quantity)

                        Toast.makeText(
                            context,
                            "$quantity × ${current.title} ajouté au panier",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        }
    }
}


/* =========================================================
   CONTENU DE LA PAGE DÉTAIL
   ========================================================= */

@Composable
private fun CoffeeDetailContent(
    coffee: Coffee,
    isFavorite: Boolean,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onAddToCart: (Int) -> Unit
) {

    var quantity by remember {
        mutableIntStateOf(1)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // =================================================
        // PAGE QUI DÉFILE
        // =================================================

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // ---------- EN-TÊTE TERRACOTTA + IMAGE ----------

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 900.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                val heroHeight = (maxWidth * 1.05f).coerceIn(300.dp, 430.dp)
                val coffeeImageSize = (maxWidth * 0.62f).coerceIn(180.dp, 250.dp)
                val titleFontSize = (maxWidth * 0.14f).value.coerceIn(30f, 56f).sp

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(heroHeight)
                ) {

                    // Fond terracotta
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(heroHeight * 0.70f)
                            .background(CoffeeColors.Accent)
                    )

                    // Titre en filigrane
                    Text(
                        text = coffee.title,

                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(top = 42.dp),

                        color = Color.White.copy(alpha = 0.28f),
                        fontSize = titleFontSize,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Clip
                    )

                    // Feuille sombre arrondie
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(heroHeight * 0.45f)
                            .background(
                                CoffeeColors.Background,
                                RoundedCornerShape(
                                    topStart = 44.dp,
                                    topEnd = 44.dp
                                )
                            )
                    )

                    // Image ronde
                    AsyncImage(
                        model = coffee.image,

                        contentDescription = coffee.title,

                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = heroHeight * 0.35f)
                            .size(coffeeImageSize)
                            .clip(CircleShape)
                            .border(
                                width = 6.dp,
                                color = CoffeeColors.Background,
                                shape = CircleShape
                            )
                            .background(CoffeeColors.SurfaceHigh),

                        contentScale = ContentScale.Crop
                    )

                    // Bouton retour
                    CircleIconButton(
                        onClick = onBack,

                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(start = 16.dp, top = 8.dp)
                    ) {

                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Retour",
                            tint = Color.White
                        )
                    }

                    // Bouton favori (le cœur s'ajoute / se retire des favoris)
                    CircleIconButton(
                        onClick = onToggleFavorite,

                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .statusBarsPadding()
                            .padding(end = 16.dp, top = 8.dp)
                    ) {

                        Icon(
                            imageVector =
                                if (isFavorite) Icons.Default.Favorite
                                else Icons.Default.FavoriteBorder,

                            contentDescription =
                                if (isFavorite) "Retirer des favoris"
                                else "Ajouter aux favoris",

                            tint =
                                if (isFavorite) CoffeeColors.Accent
                                else Color.White
                        )
                    }
                }
            }

            // ---------- CONTENU ----------

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 760.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 22.dp)
            ) {

                // Nom + type
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = coffee.title,
                        color = CoffeeColors.OnDark,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(CoffeeColors.Surface)
                            .padding(
                                horizontal = 14.dp,
                                vertical = 7.dp
                            )
                    ) {

                        Text(
                            text = coffee.type.label,
                            color = CoffeeColors.Accent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Description
                if (coffee.description.isNotBlank()) {

                    Text(
                        text = coffee.description,
                        color = CoffeeColors.Muted,
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(22.dp))
                }

                // Ingrédients (lecture seule)
                Text(
                    text = "Ingrédients",
                    color = CoffeeColors.OnDark,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),

                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    coffee.ingredients.forEach { raw ->

                        val optional = raw.endsWith("*")

                        val name = raw.removeSuffix("*").trim()

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(CoffeeColors.Surface)
                                .padding(
                                    horizontal = 14.dp,
                                    vertical = 8.dp
                                )
                        ) {

                            Text(
                                text =
                                    if (optional) "$name (optionnel)"
                                    else name,

                                color = CoffeeColors.OnDark,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Quantité
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Quantité",
                        color = CoffeeColors.OnDark,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    QuantityStepper(
                        quantity = quantity,
                        onMinus = {
                            if (quantity > 1) quantity--
                        },
                        onPlus = {
                            quantity++
                        }
                    )
                }

                // Espace pour ne pas masquer le contenu derrière le bouton fixe
                Spacer(modifier = Modifier.height(120.dp))
            }
        }

        // =================================================
        // BOUTON FIXE EN BAS : AJOUTER AU PANIER
        // =================================================

        Button(
            onClick = {
                onAddToCart(quantity)
                quantity = 1
            },

            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(
                    start = 22.dp,
                    end = 22.dp,
                    bottom = 18.dp
                )
                .fillMaxWidth()
                .height(58.dp),

            shape = CircleShape,

            colors = ButtonDefaults.buttonColors(
                containerColor = CoffeeColors.Accent
            )
        ) {

            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                tint = Color.White
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Ajouter au panier",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}


/* =========================================================
   BOUTON ROND (retour / favori)
   ========================================================= */

@Composable
private fun CircleIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {

    Box(
        modifier = modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.35f))
            .clickable { onClick() },

        contentAlignment = Alignment.Center
    ) {

        content()
    }
}
