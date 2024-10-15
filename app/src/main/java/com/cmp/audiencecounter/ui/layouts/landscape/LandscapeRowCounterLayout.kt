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
import androidx.compose.runtime.snapshots.SnapshotStateList
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
    rowCounts: SnapshotStateList<Int>,
    formattedDateTime: String,
    savedAudiences: List<Pair<String, Int>>,
    onSaveTotal: (List<Pair<String, Int>>) -> Unit,
    onChangeRowCount: (Int) -> Unit,
    onChangeCurrentRow: (Int) -> Unit,
    onChangePeopleInRow: (Int) -> Unit,
    onShowingDialog: (Boolean) -> Unit,
    onCounting: (Boolean) -> Unit,
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
                    isEnabled = savedAudiences.isNotEmpty(),
                    onClick = { onShowingDialog(true) },
                    modifier = Modifier.align(Alignment.End)
                )

                ConfirmationDialog(
                    showDialog = showDialog,
                    onDismiss = { onShowingDialog(false) },
                    onConfirm = {
                        onSaveTotal(emptyList())
                        onShowingDialog(false)
                    },
                    title = stringResource(R.string.confirmation_dialog_title),
                    message = stringResource(R.string.confirmation_dialog_message)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botão para iniciar uma nova contagem
                Button(
                    onClick = {
                        onCounting(true)
                        onChangeRowCount(0)
                        onChangeCurrentRow(1)
                        onChangePeopleInRow(0)
                        rowCounts.clear()
                    },
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
                            onChangeRowCount(input.toIntOrNull() ?: 0)
                            onChangeCurrentRow(1)
                            rowCounts.clear()
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
                            onClick = {
                                if (currentRow >= rowCount) onCounting(false)
                                rowCounts.add(peopleInRow) // Adiciona a contagem da fileira à memória
                                onChangePeopleInRow(0) // Reseta a contagem da próxima fileira
                                onChangeCurrentRow(currentRow + 1) // Vai para a próxima fileira
                            }
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
                                onClick = { onChangePeopleInRow(0) },
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
                                onClick = { if (peopleInRow > 0) onChangePeopleInRow(peopleInRow - 1) },
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
                                onClick = { onChangePeopleInRow(peopleInRow + 1) },
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
                    val total = rowCounts.sum()
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
                        onClick = {
                            val updatedList = savedAudiences.toMutableList().apply {
                                add(0, formattedDateTime to rowCounts.sum())
                                if (size > 100) removeLast()
                            }
                            onSaveTotal(updatedList)
                            rowCounts.clear()
                            onChangeRowCount(0)
                            onCounting(false)
                        },
                        enabled = (rowCount < currentRow) // Só habilita ao final da contagem de todas as fileiras
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
        formattedDateTime = "",
        savedAudiences = listOf("12/09/2024 14:35" to 100, "11/09/2024 15:10" to 80),
        onSaveTotal = {},
        onChangeRowCount = {},
        onChangeCurrentRow = {},
        onChangePeopleInRow = {},
        onShowingDialog = {},
        onCounting = {}
    )
}