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
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.cmp.audiencecounter.ui.components.Footer
import com.cmp.audiencecounter.ui.components.NextRowButton
import com.cmp.audiencecounter.ui.components.NumberInputField
import com.cmp.audiencecounter.ui.components.ResetButton
import com.cmp.audiencecounter.ui.components.RowCountDisplay
import com.cmp.audiencecounter.ui.components.SavedAudiencesDisplay
import com.cmp.audiencecounter.utils.getCurrentFormattedDate

@Composable
fun RowCounterLayout(
    savedAudiences: List<Pair<String, Int>>,
    onSaveTotal: (List<Pair<String, Int>>) -> Unit
) {
    var rowCount by remember { mutableIntStateOf(0) } // Quantidade de fileiras
    var currentRow by remember { mutableIntStateOf(1) } // Fileira atual
    var peopleInRow by remember { mutableIntStateOf(0) } // Pessoas na fileira atual
    val rowCounts = remember { mutableStateListOf<Int>() } // Armazena temporariamente as contagens

    var isSaveEnabled by remember { mutableStateOf(false) } // Habilita o botão salvar ao final
    var isCounting by remember { mutableStateOf(false) } // Controla se uma nova contagem está em andamento
    var showDialog by remember { mutableStateOf(false) }

    val formattedDateTime = getCurrentFormattedDate()

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
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
                    onSaveTotal(emptyList())
                    showDialog = false
                },
                title = stringResource(R.string.confirmation_dialog_title),
                message = stringResource(R.string.confirmation_dialog_message)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botão para iniciar uma nova contagem
        Button(
            onClick = {
                isCounting = true
                rowCount = 0
                currentRow = 1
                peopleInRow = 0
                rowCounts.clear()
                isSaveEnabled = false
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(stringResource(R.string.start_new_count))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Input para número de fileiras se a contagem estiver em andamento
        if (isCounting) {

            NumberInputField(
                value = if (rowCount == 0) "" else rowCount.toString(),
                onValueChange = { input ->
                    rowCount = input.toIntOrNull() ?: 0
                    currentRow = 1
                    rowCounts.clear()
                    isSaveEnabled = false
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Se houver fileiras a contar
            if (rowCount > 0 && currentRow <= rowCount) {
                Text(stringResource(R.string.row_count_text, currentRow))
                Text(peopleInRow.toString(),fontSize = 32.sp)

                Spacer(modifier = Modifier.height(8.dp))

                // Botão para salvar a contagem da fileira e ir para a próxima
                NextRowButton(
                    currentRow = currentRow,
                    rowCount = rowCount,
                    onClick = {
                        rowCounts.add(peopleInRow) // Adiciona a contagem da fileira à memória
                        peopleInRow = 0 // Reseta a contagem da próxima fileira
                        currentRow++ // Vai para a próxima fileira
                        if (currentRow > rowCount) {
                            isSaveEnabled = true // Habilita salvar após última fileira
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                RowCountDisplay(rowCounts)

                Spacer(modifier = Modifier.height(16.dp))

                // Contagem de pessoas na fileira atual
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CounterButton(
                        text = "-",
                        onClick = { if (peopleInRow > 0) peopleInRow-- },
                        backgroundColor = Color(237 / 255f, 130 / 255f, 86 / 255f),
                        containerColor = Color(237 / 255f, 130 / 255f, 86 / 255f),
                        modifier = Modifier.align(Alignment.Bottom),
                        size = 60.dp,
                        fontSize = 24.sp,
                        shadowShapeSize = 16.dp,
                        contentColor = Color.White
                    )

                    Spacer(modifier = Modifier.width(64.dp))

                    CounterButton(
                        text = "+",
                        onClick = { peopleInRow++ },
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

        Spacer(modifier = Modifier.height(8.dp))

        // Exibe o total das fileiras contadas quando todas forem contadas
        if (rowCounts.size == rowCount && rowCount > 0) {
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
        }

        if (rowCount > 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                // Botão para zerar o contador
                ResetButton(
                    onClick = { peopleInRow = 0 },
                    enabled = isCounting && currentRow <= rowCount
                )

                // Botão para salvar o total quando todas as fileiras forem contadas
                Button(
                    onClick = {
                        val updatedList = savedAudiences.toMutableList().apply {
                            add(0, formattedDateTime to rowCounts.sum())
                            if (size > 100) removeLast()
                        }
                        onSaveTotal(updatedList)
                        rowCounts.clear()
                        rowCount = 0
                        isCounting = false
                        isSaveEnabled = false
                    },
                    enabled = isSaveEnabled // Só habilita ao final da contagem de todas as fileiras
                ) {
                    Text(stringResource(R.string.save_total))
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f)) // Empurra o rodapé para baixo

        Footer(fontSize = 8.sp)
    }
}