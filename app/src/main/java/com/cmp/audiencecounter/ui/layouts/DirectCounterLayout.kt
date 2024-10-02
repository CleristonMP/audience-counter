package com.cmp.audiencecounter.ui.layouts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.cmp.audiencecounter.utils.getCurrentFormattedDate

@Composable
fun DirectCounterLayout(
    savedAudiences: List<Pair<String, Int>>,
    onSaveAudiences: (List<Pair<String, Int>>) -> Unit
) {
    var audience by remember { mutableIntStateOf(0) }
    var isSaving by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    val formattedDateTime = getCurrentFormattedDate()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.app_title),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Coluna para exibir as assistências salvas
        Column {
            SavedAudiencesDisplay(
                savedAudiences = savedAudiences,
                displayHeight = 68.dp,
                titleFontWeight = FontWeight.Bold
            )

            ClearButton(
                isEnabled = savedAudiences.isNotEmpty(),
                onClick = { showDialog = true },
                modifier = Modifier.align(Alignment.End)
            )

            ConfirmationDialog(
                showDialog = showDialog,
                onDismiss = { showDialog = false },
                onConfirm = {
                    onSaveAudiences(emptyList())
                    showDialog = false
                },
                title = stringResource(R.string.confirmation_dialog_title),
                message = stringResource(R.string.confirmation_dialog_message)
            )
        }

        Spacer(modifier = Modifier.height(72.dp))

        CounterDisplay(audience)

        Spacer(modifier = Modifier.height(72.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            ResetButton(onClick = { audience = 0 })

            SaveButton(
                onClick = {
                    if (!isSaving) {
                        isSaving = true // Impede que múltiplos cliques sejam registrados
                        val updatedList = savedAudiences.toMutableList().apply {
                            add(0, formattedDateTime to audience)
                            if (size > 100) removeLast()
                        }
                        onSaveAudiences(updatedList)
                        audience = 0
                        isSaving = false
                    }
                },
                isSaving = isSaving
            )
        }

        Spacer(modifier = Modifier.height(68.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            CounterButton(
                text = "-",
                onClick = { if (audience > 0) audience-- },
                backgroundColor = Color(237 / 255f, 130 / 255f, 86 / 255f),
                containerColor = Color(237 / 255f, 130 / 255f, 86 / 255f),
                size = 60.dp,
                fontSize = 24.sp,
                shadowElevation = 12.dp,
                shadowShapeSize = 16.dp,
                contentColor = Color.White
            )

            Spacer(modifier = Modifier.width(64.dp))

            CounterButton(
                text = "+",
                onClick = { audience++ },
                backgroundColor = Color(73 / 255f, 116 / 255f, 145 / 255f),
                containerColor = Color(73 / 255f, 116 / 255f, 145 / 255f),
                size = 120.dp,
                fontSize = 48.sp,
                shadowElevation = 12.dp,
                shadowShapeSize = 16.dp,
                contentColor = Color.White
            )
        }

        Spacer(modifier = Modifier.weight(1f)) // Empurra o rodapé para baixo

        Footer(fontSize = 12.sp)
    }
}