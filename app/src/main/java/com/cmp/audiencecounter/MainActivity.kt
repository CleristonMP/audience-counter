package com.cmp.audiencecounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.cmp.audiencecounter.datastore.AudienceCounterDataStore
import com.cmp.audiencecounter.ui.components.CounterButton
import com.cmp.audiencecounter.ui.components.CounterDisplay
import com.cmp.audiencecounter.ui.components.Footer
import com.cmp.audiencecounter.ui.components.NextRowButton
import com.cmp.audiencecounter.ui.components.ResetButton
import com.cmp.audiencecounter.ui.components.RowCountDisplay
import com.cmp.audiencecounter.ui.components.SaveButton
import com.cmp.audiencecounter.ui.components.SavedAudiencesDisplay
import com.cmp.audiencecounter.ui.theme.AudienceCounterTheme
import com.cmp.audiencecounter.utils.getCurrentFormattedDate
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var dataStore: AudienceCounterDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataStore = AudienceCounterDataStore(applicationContext)

        setContent {
            AudienceCounterTheme {
                val savedAudiences = remember { mutableStateListOf<Pair<String, Int>>() }

                // Inicialize com as assistências recuperadas do DataStore
                LaunchedEffect(Unit) {
                    dataStore.audiencesFlow.collect { storedAudiences ->
                        savedAudiences.clear()
                        savedAudiences.addAll(storedAudiences)
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AudienceCounterWithTabs(
                        modifier = Modifier.padding(innerPadding),
                        savedAudiences = savedAudiences,
                        onSaveAudiences = { audiences ->
                            // Salva as contagens no DataStore
                            lifecycleScope.launch {
                                dataStore.saveAudiences(audiences)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AudienceCounterWithTabs(
    modifier: Modifier = Modifier,
    savedAudiences: List<Pair<String, Int>>,
    onSaveAudiences: (List<Pair<String, Int>>) -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        stringResource(R.string.direct_count_tab_title),
        stringResource(R.string.row_count_tab_title)
    )

    Column(
        modifier = modifier
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = tab,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 18.sp
                        )
                    }
                )
            }
        }

        when (selectedTabIndex) {
            // Contagem Direta
            0 -> DirectCounterLayout(
                savedAudiences = savedAudiences,
                onSaveAudiences = onSaveAudiences
            )
            // Contagem por Fileira
            1 -> RowCounterLayout(
                savedAudiences = savedAudiences,
                onSaveTotal = onSaveAudiences
            )
        }
    }
}

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

            Text(
                text = stringResource(R.string.clear_button_text),
                fontSize = 16.sp,
                color = if (savedAudiences.isEmpty()) Color.LightGray else Color.Blue,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable(
                        enabled = savedAudiences.isNotEmpty(),
                        onClick = { showDialog = true }
                    )
            )

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(stringResource(R.string.confirmation_dialog_title)) },
                    text = { Text(stringResource(R.string.confirmation_dialog_message)) },
                    confirmButton = {
                        Button(
                            onClick = {
                                onSaveAudiences(emptyList())
                                showDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.DarkGray
                            ),
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = Color.DarkGray,
                                    shape = RoundedCornerShape(16.dp)
                                )
                        ) {
                            Text(stringResource(R.string.confirm_button_text))
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showDialog = false }
                        ) {
                            Text(stringResource(R.string.cancel_button_text))
                        }
                    }
                )
            }
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

            Text(
                text = stringResource(R.string.clear_button_text),
                fontSize = 14.sp,
                color = if (savedAudiences.isEmpty()) Color.LightGray else Color.Blue,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable(
                        enabled = savedAudiences.isNotEmpty(),
                        onClick = { showDialog = true }
                    )
            )

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(stringResource(R.string.confirmation_dialog_title)) },
                    text = { Text(stringResource(R.string.confirmation_dialog_message)) },
                    confirmButton = {
                        Button(
                            onClick = {
                                onSaveTotal(emptyList())
                                showDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.DarkGray
                            ),
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = Color.DarkGray,
                                    shape = RoundedCornerShape(16.dp)
                                )
                        ) {
                            Text(stringResource(R.string.confirm_button_text))
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showDialog = false }
                        ) {
                            Text(stringResource(R.string.cancel_button_text))
                        }
                    }
                )
            }
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
            OutlinedTextField(
                value = if (rowCount == 0) "" else rowCount.toString(),
                onValueChange = { input ->
                    rowCount = input.toIntOrNull() ?: 0
                    currentRow = 1 // Reseta para a primeira fileira ao mudar o número de fileiras
                    rowCounts.clear() // Limpa contagens anteriores
                    isSaveEnabled =
                        false // Desabilita salvar até que todas as fileiras sejam contadas
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                label = { Text(stringResource(R.string.number_of_rows)) },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(128.dp),
                textStyle = TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp
                )
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
                        shadowElevation = 12.dp,
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
                        shadowElevation = 12.dp,
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

/*
@Preview(showBackground = true)
@Composable
fun AudienceCounterLayoutPreview() {
    AudienceCounterTheme {
        AudienceCounterLayout(
            savedAudiences = listOf(
                "13/09/2024 12:00:00" to 50,
                "12/09/2024 14:35:21" to 100,
                "11/09/2024 15:10:10" to 80
            ),
            onSaveAudiences = {}
        )
    }
}
*/

/*
@Preview(showBackground = true)
@Composable
fun RowCounterPreview() {
    RowCounter(
        savedAudiences = listOf(
            "13/09/2024 12:00:00" to 50,
            "12/09/2024 14:35:21" to 100,
            "11/09/2024 15:10:10" to 80
        )
    )
}
*/
