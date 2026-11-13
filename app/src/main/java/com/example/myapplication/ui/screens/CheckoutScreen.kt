package com.example.myapplication.ui.screens

import android.util.Patterns
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.model.ContactInfo
import com.example.myapplication.ui.components.StatusBarIcons
import com.example.myapplication.ui.theme.CoffeeColors
import com.example.myapplication.ui.viewmodel.CoffeeViewModel

@Composable
fun CheckoutScreen(
    onBack: () -> Unit,
    onOrderDone: () -> Unit,
    viewModel: CoffeeViewModel = viewModel()
) {

    StatusBarIcons(darkIcons = false)

    val cart by viewModel.cart.collectAsState()
    val savedContact by viewModel.contact.collectAsState()

    val totalItems = cart.sumOf { it.quantity }

    // =====================================================
    // CHAMPS (pré-remplis avec les dernières coordonnées)
    // =====================================================

    var name by remember { mutableStateOf(savedContact.name) }
    var phone by remember { mutableStateOf(savedContact.phone) }
    var email by remember { mutableStateOf(savedContact.email) }
    var address by remember { mutableStateOf(savedContact.address) }

    // Erreurs affichées seulement après un essai de validation
    var showErrors by remember { mutableStateOf(false) }

    // Message de confirmation (nom, nombre d'articles)
    var confirmation by remember {
        mutableStateOf<Pair<String, Int>?>(null)
    }

    val nameError = name.trim().length < 2
    val phoneError = !Regex("^\\+?[0-9 .-]{8,16}$").matches(phone.trim())
    val emailError = !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    val addressError = address.trim().length < 5

    val formValid =
        !nameError && !phoneError && !emailError && !addressError

    val backDispatcher =
        LocalOnBackPressedDispatcherOwner.current
            ?.onBackPressedDispatcher

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = CoffeeColors.Surface,
        unfocusedContainerColor = CoffeeColors.Surface,

        focusedBorderColor = CoffeeColors.Accent,
        unfocusedBorderColor = Color.Transparent,

        focusedTextColor = CoffeeColors.OnDark,
        unfocusedTextColor = CoffeeColors.OnDark,

        focusedLabelColor = CoffeeColors.Accent,
        unfocusedLabelColor = CoffeeColors.Muted,

        cursorColor = CoffeeColors.Accent
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CoffeeColors.Background)
            .statusBarsPadding()
            .imePadding()
    ) {

        // =================================================
        // EN-TÊTE
        // =================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(CoffeeColors.Surface)
                    .clickable { backDispatcher?.onBackPressed() ?: onBack() },

                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Retour",
                    tint = CoffeeColors.OnDark
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = "Finaliser la commande",
                color = CoffeeColors.OnDark,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        // =================================================
        // FORMULAIRE + RÉCAPITULATIF
        // =================================================

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .widthIn(max = 760.dp)
                .align(Alignment.CenterHorizontally)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp)
        ) {

            Text(
                text = "Vos coordonnées",
                color = CoffeeColors.OnDark,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nom complet") },
                singleLine = true,
                isError = showErrors && nameError,
                supportingText = {
                    if (showErrors && nameError) {
                        Text("Entrez votre nom")
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
                shape = RoundedCornerShape(18.dp),
                colors = fieldColors
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Téléphone") },
                singleLine = true,
                isError = showErrors && phoneError,
                supportingText = {
                    if (showErrors && phoneError) {
                        Text("Numéro invalide (8 à 16 chiffres)")
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                ),
                shape = RoundedCornerShape(18.dp),
                colors = fieldColors
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email") },
                singleLine = true,
                isError = showErrors && emailError,
                supportingText = {
                    if (showErrors && emailError) {
                        Text("Adresse email invalide")
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                shape = RoundedCornerShape(18.dp),
                colors = fieldColors
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Adresse de livraison") },
                minLines = 2,
                maxLines = 3,
                isError = showErrors && addressError,
                supportingText = {
                    if (showErrors && addressError) {
                        Text("Entrez une adresse complète")
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
                shape = RoundedCornerShape(18.dp),
                colors = fieldColors
            )

            Spacer(modifier = Modifier.height(18.dp))

            // ---------- RÉCAPITULATIF ----------

            Text(
                text = "Récapitulatif",
                color = CoffeeColors.OnDark,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(CoffeeColors.Surface)
                    .padding(16.dp),

                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                cart.forEach { item ->

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = item.coffee.title,
                            color = CoffeeColors.OnDark,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = "× ${item.quantity}",
                            color = CoffeeColors.Accent,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Total articles",
                        color = CoffeeColors.Muted,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "$totalItems",
                        color = CoffeeColors.OnDark,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // =================================================
        // BOUTON : VALIDER LA COMMANDE
        // =================================================

        Button(
            onClick = {

                if (cart.isNotEmpty() && formValid) {

                    val info = ContactInfo(
                        name = name.trim(),
                        phone = phone.trim(),
                        email = email.trim(),
                        address = address.trim()
                    )

                    confirmation = info.name to totalItems

                    viewModel.placeOrder(info)

                } else {

                    showErrors = true
                }
            },

            enabled = cart.isNotEmpty(),

            modifier = Modifier
                .navigationBarsPadding()
                .padding(
                    start = 18.dp,
                    end = 18.dp,
                    top = 8.dp,
                    bottom = 16.dp
                )
                .fillMaxWidth()
                .height(58.dp),

            shape = CircleShape,

            colors = ButtonDefaults.buttonColors(
                containerColor = CoffeeColors.Accent,
                disabledContainerColor = CoffeeColors.SurfaceHigh
            )
        ) {

            Text(
                text = "Valider la commande",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }

    // =====================================================
    // CONFIRMATION
    // =====================================================

    confirmation?.let { (customer, count) ->

        AlertDialog(

            onDismissRequest = {},

            containerColor = CoffeeColors.Surface,

            title = {
                Text(
                    text = "Commande validée ☕",
                    color = CoffeeColors.OnDark,
                    fontWeight = FontWeight.Bold
                )
            },

            text = {
                Text(
                    text = "Merci $customer ! Votre commande de $count article(s) a bien été enregistrée.",
                    color = CoffeeColors.Muted
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {
                        confirmation = null
                        onOrderDone()
                    }
                ) {

                    Text(
                        text = "OK",
                        color = CoffeeColors.Accent,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }
}
