package com.cmp.audiencecounter.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.cmp.audiencecounter.R

@Composable
fun NextRowButton(
    currentRow: Int,
    rowCount: Int,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = currentRow <= rowCount // Desabilita quando chegar na última fileira
    ) {
        Text(
            if (currentRow < rowCount) stringResource(R.string.next_row)
            else stringResource(R.string.finish_counting)
        )
    }
}
