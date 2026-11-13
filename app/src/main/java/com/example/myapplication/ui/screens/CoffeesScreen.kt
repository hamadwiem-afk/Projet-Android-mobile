package com.example.myapplication.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.myapplication.data.model.CoffeeType
import com.example.myapplication.ui.components.CoffeeBottomBar
import com.example.myapplication.ui.components.CoffeeCard
import com.example.myapplication.ui.components.StatusBarIcons
import com.example.myapplication.ui.theme.CoffeeColors
import com.example.myapplication.ui.viewmodel.CoffeeViewModel

@Composable
fun CoffeesScreen(
    onCoffeeClick: (String, String) -> Unit,
    onNavigate: (String) -> Unit,
    viewModel: CoffeeViewModel = viewModel()
) {
    StatusBarIcons(darkIcons = false)

    val context = LocalContext.current
    val coffees by viewModel.coffees.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val typeFilter by viewModel.typeFilter.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val cart by viewModel.cart.collectAsState()

    val uniqueCoffees = coffees.distinctBy { it.key }
    val favoriteKeys = favorites.map { it.key }.toSet()
    val cartCount = cart.sumOf { it.quantity }

    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadAllCoffees()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CoffeeColors.Background)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            // Sur les grands écrans, les filtres restent à gauche et les boissons
            // occupent toute la partie droite. Sur téléphone, on garde une colonne.
            val isWideScreen = maxWidth >= 700.dp

            if (isWideScreen) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 24.dp, end = 24.dp, top = 18.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .width(230.dp)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(24.dp))
                            .background(CoffeeColors.Surface)
                            .padding(18.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        AppTitle()
                        Spacer(modifier = Modifier.height(22.dp))
                        SearchField(
                            searchText = searchText,
                            onSearchChange = { text ->
                                searchText = text
                                viewModel.searchCoffees(text)
                            }
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Catégories",
                            color = CoffeeColors.OnDark,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        CategoryFilters(
                            typeFilter = typeFilter,
                            onSelect = { viewModel.selectTypeFilter(it) },
                            vertical = true
                        )
                    }

                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        when {
                            isLoading -> LoadingContent()
                            error != null && coffees.isEmpty() -> ErrorContent(
                                message = error ?: "Une erreur est survenue.",
                                onRetry = { viewModel.loadAllCoffees() }
                            )
                            else -> CoffeeGrid(
                                coffees = uniqueCoffees,
                                searchText = searchText,
                                favoriteKeys = favoriteKeys,
                                onCoffeeClick = onCoffeeClick,
                                onFavoriteClick = { viewModel.toggleFavorite(it) },
                                onAddToCart = { coffee ->
                                    viewModel.addToCart(coffee)
                                    Toast.makeText(
                                        context,
                                        "${coffee.title} ajouté au panier",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                showCategories = false
                            )
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 18.dp)
                ) {
                    Spacer(modifier = Modifier.height(14.dp))
                    AppTitle()
                    Spacer(modifier = Modifier.height(14.dp))
                    SearchField(
                        searchText = searchText,
                        onSearchChange = { text ->
                            searchText = text
                            viewModel.searchCoffees(text)
                        }
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    when {
                        isLoading -> LoadingContent()
                        error != null && coffees.isEmpty() -> ErrorContent(
                            message = error ?: "Une erreur est survenue.",
                            onRetry = { viewModel.loadAllCoffees() }
                        )
                        else -> CoffeeGrid(
                            coffees = uniqueCoffees,
                            searchText = searchText,
                            favoriteKeys = favoriteKeys,
                            onCoffeeClick = onCoffeeClick,
                            onFavoriteClick = { viewModel.toggleFavorite(it) },
                            onAddToCart = { coffee ->
                                viewModel.addToCart(coffee)
                                Toast.makeText(
                                    context,
                                    "${coffee.title} ajouté au panier",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            showCategories = true,
                            typeFilter = typeFilter,
                            onSelectType = { viewModel.selectTypeFilter(it) }
                        )
                    }
                }
            }
        }

        CoffeeBottomBar(
            selected = "coffees",
            cartCount = cartCount,
            onNavigate = onNavigate
        )
    }
}

@Composable
private fun AppTitle() {
    Row(verticalAlignment = Alignment.Bottom) {
        Text(
            text = "Coffee",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = CoffeeColors.OnDark
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "Vibe",
            fontSize = 22.sp,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
            color = CoffeeColors.Accent,
            modifier = Modifier.padding(bottom = 2.dp)
        )
    }
}

@Composable
private fun SearchField(
    searchText: String,
    onSearchChange: (String) -> Unit
) {
    OutlinedTextField(
        value = searchText,
        onValueChange = onSearchChange,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        placeholder = {
            Text(
                text = "Rechercher...",
                color = CoffeeColors.Muted,
                maxLines = 1
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Rechercher",
                tint = CoffeeColors.Muted
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = CoffeeColors.Background,
            unfocusedContainerColor = CoffeeColors.Background,
            focusedBorderColor = CoffeeColors.Accent,
            unfocusedBorderColor = Color.Transparent,
            focusedTextColor = CoffeeColors.OnDark,
            unfocusedTextColor = CoffeeColors.OnDark,
            cursorColor = CoffeeColors.Accent
        )
    )
}

@Composable
private fun CategoryFilters(
    typeFilter: CoffeeType?,
    onSelect: (CoffeeType?) -> Unit,
    vertical: Boolean
) {
    val chips: @Composable () -> Unit = {
        FilterChip("Tous", null, typeFilter == null) { onSelect(null) }
        FilterChip("Chauds", Icons.Default.LocalCafe, typeFilter == CoffeeType.HOT) {
            onSelect(CoffeeType.HOT)
        }
        FilterChip("Froids", Icons.Default.AcUnit, typeFilter == CoffeeType.ICED) {
            onSelect(CoffeeType.ICED)
        }
    }

    if (vertical) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) { chips() }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) { chips() }
    }
}

@Composable
private fun LoadingContent() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = CoffeeColors.Accent)
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "☕", fontSize = 45.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                color = CoffeeColors.OnDark,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRetry,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = CoffeeColors.Accent)
            ) {
                Text("Réessayer", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun CoffeeGrid(
    coffees: List<Coffee>,
    searchText: String,
    favoriteKeys: Set<String>,
    onCoffeeClick: (String, String) -> Unit,
    onFavoriteClick: (Coffee) -> Unit,
    onAddToCart: (Coffee) -> Unit,
    showCategories: Boolean,
    typeFilter: CoffeeType? = null,
    onSelectType: (CoffeeType?) -> Unit = {}
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 155.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (!showCategories && searchText.isBlank() && coffees.isNotEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                FeaturedBanner(coffee = coffees.first()) {
                    onCoffeeClick(coffees.first().type.path, coffees.first().id)
                }
            }
        }

        if (showCategories) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    Text(
                        text = "Catégories",
                        color = CoffeeColors.OnDark,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    CategoryFilters(typeFilter, onSelectType, vertical = false)
                }
            }
            if (searchText.isBlank() && coffees.isNotEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    FeaturedBanner(coffee = coffees.first()) {
                        onCoffeeClick(coffees.first().type.path, coffees.first().id)
                    }
                }
            }
        }

        if (coffees.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucun café trouvé",
                        color = CoffeeColors.OnDark,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        items(items = coffees, key = { it.key }) { coffee ->
            CoffeeCard(
                coffee = coffee,
                isFavorite = coffee.key in favoriteKeys,
                onClick = { onCoffeeClick(coffee.type.path, coffee.id) },
                onFavoriteClick = { onFavoriteClick(coffee) },
                onAddToCart = { onAddToCart(coffee) }
            )
        }
    }
}

/* =========================================================
   BANNIÈRE : CAFÉ DU MOMENT
   ========================================================= */

@Composable
private fun FeaturedBanner(
    coffee: Coffee,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(CoffeeColors.Surface)
            .clickable { onClick() }
            .padding(
                start = 20.dp,
                top = 16.dp,
                bottom = 16.dp,
                end = 14.dp
            ),

        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(modifier = Modifier.weight(1f)) {

            Text(
                text = "Café du moment",
                color = CoffeeColors.Accent,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = coffee.title,
                color = CoffeeColors.OnDark,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 27.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(CoffeeColors.Accent)
                    .padding(
                        horizontal = 18.dp,
                        vertical = 8.dp
                    )
            ) {
                Text(
                    text = "Découvrir",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        AsyncImage(
            model = coffee.image,
            contentDescription = coffee.title,
            modifier = Modifier
                .size(112.dp)
                .clip(CircleShape)
                .background(CoffeeColors.SurfaceHigh),
            contentScale = ContentScale.Crop
        )
    }
}


/* =========================================================
   CHIP DE CATÉGORIE
   ========================================================= */

@Composable
private fun FilterChip(
    label: String,
    icon: ImageVector?,
    selected: Boolean,
    onClick: () -> Unit
) {

    val background =
        if (selected) CoffeeColors.Accent else CoffeeColors.Surface

    val content =
        if (selected) Color.White else CoffeeColors.Muted

    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(background)
            .clickable { onClick() }
            .padding(
                horizontal = 16.dp,
                vertical = 10.dp
            ),

        verticalAlignment = Alignment.CenterVertically
    ) {

        if (icon != null) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = content,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))
        }

        Text(
            text = label,
            color = content,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
