package com.cmp.audiencecounter.ui.layouts.landscape

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmp.audiencecounter.R
import com.cmp.audiencecounter.ui.components.ClearButton
import com.cmp.audiencecounter.ui.components.ConfirmationDialog
import com.cmp.audiencecounter.ui.components.CounterButton
import com.cmp.audiencecounter.ui.components.CounterDisplay
import com.cmp.audiencecounter.ui.components.Footer
import com.cmp.audiencecounter.ui.components.ResetButton
import com.cmp.audiencecounter.ui.components.SaveButton
import com.cmp.audiencecounter.ui.components.SavedAudiencesDisplay

@Composable
fun LandscapeDirectCounterLayout(
    savedAudiences: List<Pair<String, Int>>,
    audience: Int,
    showDialog: Boolean,
    isSaving: Boolean,
    onResetCounting: () -> Unit,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    onSave: () -> Unit,
    onClearAudiences: () -> Unit,
    onShowingDialog: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                SavedAudiencesDisplay(
                    savedAudiences = savedAudiences,
                    displayHeight = 68.dp,
                    titleFontWeight = FontWeight.Bold
                )

                ClearButton(
                    isEnabled = savedAudiences.isNotEmpty() && !isSaving,
                    onClick = { onShowingDialog(true) },
                    modifier = Modifier.align(Alignment.End)
                )

                ConfirmationDialog(
                    showDialog = showDialog,
                    onDismiss = { onShowingDialog(false) },
                    onConfirm = {
                        onClearAudiences()
                        onShowingDialog(false)
                    },
                    title = stringResource(R.string.confirmation_dialog_title),
                    message = stringResource(R.string.confirmation_dialog_message)
                )

                Spacer(modifier = Modifier.height(20.dp))

                CounterDisplay(audience)
            }

            Spacer(modifier = Modifier.width(72.dp))

            Column {
                Row {
                    ResetButton(onClick = onResetCounting)

                    SaveButton(
                        onClick = onSave,
                        isSaving = isSaving,
                        audience = audience
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row {
                    CounterButton(
                        text = "-",
                        onClick = { if (audience > 0) onDecrement() },
                        backgroundColor = Color(237 / 255f, 130 / 255f, 86 / 255f),
                        containerColor = Color(237 / 255f, 130 / 255f, 86 / 255f),
                        size = 60.dp,
                        fontSize = 24.sp,
                        shadowShapeSize = 16.dp,
                        contentColor = Color.White
                    )

                    Spacer(modifier = Modifier.width(64.dp))

                    CounterButton(
                        text = "+",
                        onClick = onIncrement,
                        backgroundColor = Color(73 / 255f, 116 / 255f, 145 / 255f),
                        containerColor = Color(73 / 255f, 116 / 255f, 145 / 255f),
                        size = 120.dp,
                        fontSize = 48.sp,
                        shadowShapeSize = 16.dp,
                        contentColor = Color.White
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f)) // Empurra o rodapé para baixo
            Footer(fontSize = 12.sp)
        }
    }
}


@Preview(showBackground = true, widthDp = 720, heightDp = 360)
@Composable
fun LandscapeDirectCounterLayoutPreview() {
    LandscapeDirectCounterLayout(
        savedAudiences = listOf("12/09/2024 14:35" to 100, "11/09/2024 15:10" to 80),
        audience = 5,
        showDialog = false,
        isSaving = false,
        onResetCounting = {},
        onDecrement = {},
        onIncrement = {},
        onSave = {},
        onClearAudiences = {},
        onShowingDialog = {},
    )
}
