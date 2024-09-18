package com.cmp.audiencecounter

import android.os.Bundle
import android.text.format.DateFormat
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.cmp.audiencecounter.datastore.AudienceCounterDataStore
import com.cmp.audiencecounter.ui.theme.AudienceCounterTheme
import kotlinx.coroutines.launch
import java.util.Date

class MainActivity : ComponentActivity() {

    private lateinit var dataStore: AudienceCounterDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataStore = AudienceCounterDataStore(applicationContext)

//        enableEdgeToEdge()
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
                    AudienceCounterLayout(
                        modifier = Modifier.padding(innerPadding),
                        savedAudiences = savedAudiences,
                        onSaveAudiences = { audiences ->
                            // Salvar as contagens no DataStore
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
fun AudienceCounterLayout(
    modifier: Modifier = Modifier,
    savedAudiences: List<Pair<String, Int>>,
    onSaveAudiences: (List<Pair<String, Int>>) -> Unit
) {
    var audience by remember { mutableIntStateOf(0) }
    var isSaving by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    val formattedDateTime = with(LocalContext.current) {
        val formatter = DateFormat.getDateFormat(this)
        val timeFormatter = DateFormat.getTimeFormat(this)
        formatter.format(Date()) + " " + timeFormatter.format(Date())
    }

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
            Text(
                text = if (savedAudiences.isEmpty()) {
                    stringResource(R.string.empty_audience_list)
                } else {
                    stringResource(R.string.saved_audience_display_text)
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            LazyColumn(
                modifier = Modifier
                    .height(68.dp) // Define a altura fixa da caixa
            ) {
                if (savedAudiences.isEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(68.dp))
                    }
                } else {
                    items(savedAudiences) { (dateTime, count) ->
                        Text(
                            stringResource(
                                R.string.saved_audiences_presentation_text,
                                dateTime,
                                count
                            )
                        )
                    }
                }
            }

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
                            }
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

        Spacer(modifier = Modifier.height(104.dp))

        // Display do número de assistências
        Box(
            modifier = Modifier
                .width(256.dp)
                .height(80.dp)
                .border(
                    width = 2.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = audience.toString(),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(modifier = Modifier.height(128.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Botão para zerar o contador
            Button(
                onClick = { audience = 0 },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.DarkGray
                ),
                modifier = Modifier.border(
                    width = 1.dp,
                    color = Color.DarkGray,
                    shape = RoundedCornerShape(16.dp)
                )
            ) {
                Text(text = stringResource(R.string.reset_button_text))
            }

            // Botão para salvar a última assistência
            Button(
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
                enabled = !isSaving, // Desativa o botão enquanto está salvando
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    stringResource(R.string.save_button_text),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            // Botão de decremento de assistências
            Button(
                onClick = { if (audience > 0) audience-- },
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(16.dp),
                        clip = true
                    )
                    .background(Color(237 / 255f, 130 / 255f, 86 / 255f))
                    .align(Alignment.Bottom),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(237 / 255f, 130 / 255f, 86 / 255f),
                    contentColor = Color.White
                )
            ) {
                Text(text = "-", fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(64.dp))

            // Botão de incremento de assistências
            Button(
                onClick = { audience++ },
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(16.dp),
                        clip = true
                    )
                    .background(Color(73 / 255f, 116 / 255f, 145 / 255f)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(73 / 255f, 116 / 255f, 145 / 255f),
                    contentColor = Color.White
                )
            ) {
                Text(text = "+", fontSize = 48.sp)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.weight(1f)) // Empurra o rodapé para baixo
        Text(
            text = stringResource(R.string.developer_credits),
            fontSize = 12.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
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