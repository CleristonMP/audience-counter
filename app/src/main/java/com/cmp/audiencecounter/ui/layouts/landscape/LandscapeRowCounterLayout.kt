package com.cmp.audiencecounter.ui.layouts.landscape

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmp.audiencecounter.R
import com.cmp.audiencecounter.model.AudienceRecord
import com.cmp.audiencecounter.ui.components.ClearButton
import com.cmp.audiencecounter.ui.components.ConfirmationDialog
import com.cmp.audiencecounter.ui.components.CounterButton
import com.cmp.audiencecounter.ui.components.Footer
import com.cmp.audiencecounter.ui.components.NextRowButton
import com.cmp.audiencecounter.ui.components.NumberInputField
import com.cmp.audiencecounter.ui.components.ResetButton
import com.cmp.audiencecounter.ui.components.RowCountDisplay
import com.cmp.audiencecounter.ui.components.SavedAudiencesDisplay

@Composable
fun LandscapeRowCounterLayout(
    showDialog: Boolean,
    isCounting: Boolean,
    rowCount: Int,
    currentRow: Int,
    peopleInRow: Int,
    rowCounts: List<Int>,
    savedAudiences: List<AudienceRecord>,
    isSaving: Boolean,
    onSaveTotal: () -> Unit,
    onClearAudiences: () -> Unit,
    onStartCounting: () -> Unit,
    onChangeRowCount: (Int) -> Unit,
    onCompleteCurrentRow: () -> Unit,
    onResetCurrentRow: () -> Unit,
    onDecrementCurrentRow: () -> Unit,
    onIncrementCurrentRow: () -> Unit,
    onShowingDialog: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Coluna para exibir as assistências salvas
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

                Spacer(modifier = Modifier.height(16.dp))

                // Botão para iniciar uma nova contagem
                Button(
                    enabled = !isSaving,
                    onClick = onStartCounting,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(stringResource(R.string.start_new_count))
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            if (isCounting) {
                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Input para número de fileiras se a contagem estiver em andamento
                    NumberInputField(
                        value = if (rowCount == 0) "" else rowCount.toString(),
                        onValueChange = { input ->
                            if (input.isEmpty()) {
                                onChangeRowCount(0)
                            } else {
                                input.toIntOrNull()?.let(onChangeRowCount)
                            }
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Se houver fileiras a contar
                    if (rowCount > 0 && currentRow <= rowCount) {
                        Text(
                            stringResource(R.string.row_count_text, currentRow),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        Text(
                            peopleInRow.toString(),
                            fontSize = 32.sp,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Botão para salvar a contagem da fileira e ir para a próxima
                        NextRowButton(
                            currentRow = currentRow,
                            rowCount = rowCount,
                            onClick = onCompleteCurrentRow
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (rowCount > 0 && currentRow <= rowCount) {
                        RowCountDisplay(rowCounts)

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            // Botão para zerar o contador
                            ResetButton(
                                onClick = onResetCurrentRow,
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Contagem de pessoas na fileira atual
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CounterButton(
                                text = "-",
                                onClick = onDecrementCurrentRow,
                                backgroundColor = Color(237 / 255f, 130 / 255f, 86 / 255f),
                                containerColor = Color(237 / 255f, 130 / 255f, 86 / 255f),
                                modifier = Modifier.align(Alignment.Bottom),
                                size = 60.dp,
                                fontSize = 24.sp,
                                shadowShapeSize = 16.dp,
                                contentColor = Color.White
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            CounterButton(
                                text = "+",
                                onClick = onIncrementCurrentRow,
                                backgroundColor = Color(73 / 255f, 116 / 255f, 145 / 255f),
                                containerColor = Color(73 / 255f, 116 / 255f, 145 / 255f),
                                size = 98.dp,
                                fontSize = 48.sp,
                                shadowShapeSize = 16.dp,
                                contentColor = Color.White
                            )
                        }
                    }
                }
            }

            // Exibe o total das fileiras contadas quando todas forem contadas
            if (rowCounts.size == rowCount && rowCount > 0) {
                Spacer(modifier = Modifier.width(48.dp))
                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Spacer(modifier = Modifier.height(48.dp))
                    val total = rowCounts.sumOf { it.toLong() }
                    Text(
                        stringResource(
                            R.string.total_people,
                            total
                        ),
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // Botão para salvar o total quando todas as fileiras forem contadas
                    Button(
                        onClick = onSaveTotal,
                        enabled = rowCount < currentRow && !isSaving
                    ) {
                        Text(stringResource(R.string.save_total))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f)) // Empurra o rodapé para baixo
        Footer(fontSize = 8.sp)
    }
}

@Preview(showBackground = true, widthDp = 720, heightDp = 360)
@Composable
fun LandscapeRowCounterLayoutPreview() {
    val rowCounts = remember { mutableStateListOf<Int>() }
    rowCounts.add(10)
    rowCounts.add(20)
    rowCounts.add(30)
    rowCounts.add(40)
    rowCounts.add(50)

    LandscapeRowCounterLayout(
        showDialog = false,
        isCounting = false,
        rowCount = 5,
        currentRow = 6,
        peopleInRow = 0,
        rowCounts = rowCounts,
        savedAudiences = listOf(AudienceRecord(1_726_151_700_000, 100)),
        isSaving = false,
        onSaveTotal = {},
        onClearAudiences = {},
        onStartCounting = {},
        onChangeRowCount = {},
        onCompleteCurrentRow = {},
        onResetCurrentRow = {},
        onDecrementCurrentRow = {},
        onIncrementCurrentRow = {},
        onShowingDialog = {},
    )
}
