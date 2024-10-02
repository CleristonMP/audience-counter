package com.cmp.audiencecounter.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cmp.audiencecounter.R

@Composable
fun SaveButton(
    onClick: () -> Unit,
    isSaving: Boolean
) {
    Button(
        onClick = onClick,
        enabled = !isSaving, // Desativa o botão enquanto está salvando
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {
        Text(
            stringResource(R.string.save_button_text),
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}
