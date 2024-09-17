package com.cmp.audiencecounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmp.audiencecounter.ui.theme.AudienceCounterTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AudienceCounterTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AudienceCounterLayout(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun AudienceCounterLayout(modifier: Modifier = Modifier) {
    var audience by remember { mutableIntStateOf(0) }
    var savedAudiences = remember { mutableStateListOf<Pair<String, Int>>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Contador de Assistência",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(32.dp))
        // Coluna para exibir as últimas 3 assistências salvas
        Column {
            if (savedAudiences.isEmpty()) {
                Text("Nenhuma assistência salva")
            } else {
                Text(
                    text = "Últimas 3 assistências salvas:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                savedAudiences
                    .sortedByDescending { it.second }
                    .takeLast(3)
                    .forEach { (count, dateTime) ->
                        Text("$count - $dateTime")
                    }
            }
        }

        Spacer(modifier = Modifier.height(216.dp))

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
            Button(onClick = { audience = 0 }) {
                Text(text = "Zerar")
            }

            // Botão para salvar a última assistência
            Button(onClick = {
                val currentDate = SimpleDateFormat(
                    "dd/MM/yyyy HH:mm:ss",
                    Locale.getDefault()
                )
                    .format(
                    Date()
                )
                savedAudiences.add(0, currentDate to audience) // Adiciona a nova contagem no início da lista
                if (savedAudiences.size > 3) savedAudiences.removeLast() // Limita as últimas 3 contagens
            }) {
                Text("Salvar")
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
                    .background(Color(237, 130, 86))
                    .align(Alignment.Bottom),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(237, 130, 86),
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
                    .background(Color(73, 116, 145)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(73, 116, 145),
                    contentColor = Color.White
                )
            ) {
                Text(text = "+", fontSize = 48.sp)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.weight(1f)) // Empurra o rodapé para baixo
        Text(
            text = "Criado por Cleriston Pereira",
            fontSize = 12.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AudienceCounterTheme {
        AudienceCounterLayout()
    }
}