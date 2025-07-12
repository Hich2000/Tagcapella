package com.hich2000.tagcapella.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TagCapellaButton(
    onClick: () -> Unit,
    modifier: Modifier,
    content: @Composable() (RowScope.() -> Unit)
) {
    Button(
        onClick = onClick,
        contentPadding = PaddingValues(0.dp),
        shape = CutCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        ),
        colors = ButtonColors(
            containerColor = Color.Gray,
            contentColor = Color.Black,
            disabledContainerColor = ButtonDefaults.buttonColors().disabledContainerColor,
            disabledContentColor = ButtonDefaults.buttonColors().disabledContentColor,
        ),
        modifier = modifier
    ) {
        content()
    }
}


