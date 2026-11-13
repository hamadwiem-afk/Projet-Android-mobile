package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.data.local.LocalStore
import com.example.myapplication.ui.components.StatusBarIcons
import com.example.myapplication.ui.screens.CartScreen
import com.example.myapplication.ui.screens.CheckoutScreen
import com.example.myapplication.ui.screens.CoffeeDetailScreen
import com.example.myapplication.ui.screens.CoffeesScreen
import com.example.myapplication.ui.screens.FavoritesScreen
import com.example.myapplication.ui.theme.CoffeeColors
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Favoris + panier sauvegardés sur le téléphone
        LocalStore.init(this)

        setContent {
            MyApplicationTheme {

                val navController = rememberNavController()

                // Navigation de la barre du bas (Accueil / Favoris / Panier)
                val navigateTab: (String) -> Unit = { route ->

                    navController.navigate(route) {

                        popUpTo("coffees") {
                            saveState = true
                        }

                        launchSingleTop = true
                        restoreState = true
                    }
                }

                // Ouvrir le détail d'un café
                val openCoffee: (String, String) -> Unit = { type, id ->

                    navController.navigate("coffee/$type/$id")
                }

                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {

                    composable("home") {

                        HomeScreen(
                            onExploreClick = {

                                navController.navigate("coffees")
                            }
                        )
                    }

                    composable("coffees") {

                        CoffeesScreen(
                            onCoffeeClick = openCoffee,
                            onNavigate = navigateTab
                        )
                    }

                    composable("favorites") {

                        FavoritesScreen(
                            onCoffeeClick = openCoffee,
                            onNavigate = navigateTab
                        )
                    }

                    composable("cart") {

                        CartScreen(
                            onCoffeeClick = openCoffee,
                            onCheckout = {
                                navController.navigate("checkout")
                            },
                            onNavigate = navigateTab
                        )
                    }

                    composable("checkout") {

                        CheckoutScreen(
                            onBack = {
                                navController.popBackStack()
                            },

                            // Commande validée : retour à l'accueil des cafés
                            onOrderDone = {

                                navController.navigate("coffees") {

                                    popUpTo("coffees")

                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    composable(
                        route = "coffee/{type}/{coffeeId}",
                        arguments = listOf(
                            navArgument("type") {
                                type = NavType.StringType
                            },
                            navArgument("coffeeId") {
                                type = NavType.StringType
                            }
                        )
                    ) { backStackEntry ->

                        val type =
                            backStackEntry.arguments
                                ?.getString("type")
                                ?: "hot"

                        val coffeeId =
                            backStackEntry.arguments
                                ?.getString("coffeeId")
                                ?: ""

                        CoffeeDetailScreen(
                            coffeeType = type,
                            coffeeId = coffeeId
                        )
                    }
                }
            }
        }
    }
}


/* =========================================================
   PAGE 1 : ACCUEIL (écran "Coffee Vibe")
   ========================================================= */

@Composable
fun HomeScreen(
    onExploreClick: () -> Unit
) {

    // Fond beige -> icônes de la barre d'état sombres
    StatusBarIcons(darkIcons = true)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(CoffeeColors.Beige)
    ) {
        val compact = maxWidth < 360.dp || maxHeight < 620.dp
        val titleSize = if (compact) 38.sp else 46.sp
        val subtitleSize = if (compact) 28.sp else 34.sp
        val heroHeight = (maxHeight * 0.42f).coerceIn(140.dp, 380.dp)

        // ---------- TITRE ----------

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = if (compact) 24.dp else 42.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(verticalAlignment = Alignment.Bottom) {

                Text(
                    text = "Coffee",
                    fontSize = titleSize,
                    fontWeight = FontWeight.ExtraBold,
                    color = CoffeeColors.Ink
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "Vibe",
                    fontSize = subtitleSize,
                    fontStyle = FontStyle.Italic,
                    fontFamily = FontFamily.Serif,
                    color = CoffeeColors.Accent,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Votre café parfait vous attend.",
                fontSize = 15.sp,
                color = CoffeeColors.Ink.copy(alpha = 0.75f),
                textAlign = TextAlign.Center
            )
        }

        // ---------- IMAGE ----------

        Image(
            painter = painterResource(id = R.drawable.img1),

            contentDescription = "Café",

            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = if (compact) 42.dp else 72.dp)
                .fillMaxWidth()
                .height(heroHeight),

            contentScale = ContentScale.Fit
        )

        // ---------- BOUTONS ----------

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    bottom = if (compact) 14.dp else 24.dp
                ),

            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(58.dp)
                    .clip(CircleShape)
                    .background(CoffeeColors.Ink)
                    .clickable { onExploreClick() },

                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "Commencer",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(CoffeeColors.Ink)
                    .clickable { onExploreClick() },

                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Commencer",
                    tint = Color.White
                )
            }
        }
    }
}
