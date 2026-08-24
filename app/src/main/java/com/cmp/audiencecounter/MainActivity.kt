package com.cmp.audiencecounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cmp.audiencecounter.presentation.AudienceCounterViewModel
import com.cmp.audiencecounter.repository.DataStoreAudienceRepository
import com.cmp.audiencecounter.ui.layouts.AudienceCounterWithTabs
import com.cmp.audiencecounter.ui.theme.AudienceCounterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AudienceCounterTheme {
                val repository = remember {
                    DataStoreAudienceRepository(applicationContext)
                }
                val counterViewModel: AudienceCounterViewModel = viewModel(
                    factory = remember(repository) {
                        AudienceCounterViewModel.Factory(repository)
                    }
                )
                val uiState by counterViewModel.uiState.collectAsStateWithLifecycle()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AudienceCounterWithTabs(
                        modifier = Modifier.padding(innerPadding),
                        uiState = uiState,
                        onAction = counterViewModel::onAction
                    )
                }
            }
        }
    }
}
