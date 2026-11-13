package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.CoffeeColors

@Composable
fun QuantityStepper(
    quantity: Int,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        StepButton(
            icon = {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Moins",
                    tint = CoffeeColors.OnDark,
                    modifier = Modifier.size(18.dp)
                )
            },
            onClick = onMinus
        )

        Box(
            modifier = Modifier.width(36.dp),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "$quantity",
                color = CoffeeColors.OnDark,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        StepButton(
            icon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Plus",
                    tint = CoffeeColors.OnDark,
                    modifier = Modifier.size(18.dp)
                )
            },
            onClick = onPlus
        )
    }
}

@Composable
private fun StepButton(
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(CoffeeColors.Accent)
            .clickable { onClick() },

        contentAlignment = Alignment.Center
    ) {

        icon()
    }
}
