package com.cmp.audiencecounter.ui.components

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
import com.cmp.audiencecounter.ui.theme.CounterContent

@Composable
fun CounterButton(
    text: String,
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp,
    fontSize: TextUnit,
    cornerRadius: Dp = 0.dp,
    contentColor: Color = CounterContent
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(cornerRadius)),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = contentColor
        )
    ) {
        Text(text, fontSize = fontSize)
    }
}
