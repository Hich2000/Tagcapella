package com.hich2000.tagcapella.utils.composables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun TagCapellaButton(
    onClick: () -> Unit,
    modifier: Modifier,
    shape: Shape = CutCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    ),
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    disabledContainerColor: Color = ButtonDefaults.buttonColors().disabledContainerColor,
    disabledContentColor: Color = ButtonDefaults.buttonColors().disabledContentColor,
    content: @Composable() (RowScope.() -> Unit)
) {
    Button(
        onClick = onClick,
        contentPadding = PaddingValues(0.dp),
        shape = shape,
        colors = ButtonColors(
            containerColor,
            contentColor,
            disabledContainerColor,
            disabledContentColor,
        ),
        modifier = modifier
    ) {
        content()
    }
}


