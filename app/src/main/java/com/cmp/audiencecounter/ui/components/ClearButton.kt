package com.cmp.audiencecounter.ui.components


import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.cmp.audiencecounter.R
import com.cmp.audiencecounter.ui.theme.DestructiveAction
import com.cmp.audiencecounter.ui.theme.DisabledAction

@Composable
fun ClearButton(
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(R.string.clear_button_text),
        fontSize = 16.sp,
        color = if (isEnabled) DestructiveAction else DisabledAction,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .clickable(enabled = isEnabled, onClick = onClick)
    )
}
