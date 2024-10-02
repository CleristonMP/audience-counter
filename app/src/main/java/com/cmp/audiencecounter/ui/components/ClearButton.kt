package com.cmp.audiencecounter.ui.components


import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.cmp.audiencecounter.R

@Composable
fun ClearButton(
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(R.string.clear_button_text),
        fontSize = 16.sp,
        color = if (isEnabled) Color.Blue else Color.LightGray,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .clickable(enabled = isEnabled, onClick = onClick)
    )
}
