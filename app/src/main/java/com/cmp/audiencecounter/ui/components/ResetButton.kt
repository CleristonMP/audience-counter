package com.cmp.audiencecounter.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cmp.audiencecounter.R
import com.cmp.audiencecounter.ui.theme.SecondaryAction

@Composable
fun ResetButton(
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = SecondaryAction
        ),
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .border(
                width = 1.dp,
                color = SecondaryAction,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Text(stringResource(R.string.reset_button_text))
    }
}
