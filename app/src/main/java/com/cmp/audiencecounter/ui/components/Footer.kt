package com.cmp.audiencecounter.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import com.cmp.audiencecounter.R


@Composable
fun Footer(fontSize: TextUnit) {
    Text(
        text = stringResource(R.string.developer_credits),
        fontSize = fontSize
    )
}