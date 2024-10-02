package com.cmp.audiencecounter.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@Composable
fun CounterButton(
    text: String,
    onClick: () -> Unit,
    backgroundColor: Color,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    size: Dp,
    fontSize: TextUnit,
    shadowShapeSize: Dp = 0.dp,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(shadowShapeSize))
            .background(backgroundColor),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Text(text, fontSize = fontSize)
    }
}
