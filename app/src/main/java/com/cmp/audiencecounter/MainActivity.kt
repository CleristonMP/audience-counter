package com.cmp.audiencecounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.cmp.audiencecounter.datastore.AudienceCounterDataStore
import com.cmp.audiencecounter.ui.layouts.AudienceCounterWithTabs
import com.cmp.audiencecounter.ui.theme.AudienceCounterTheme
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
