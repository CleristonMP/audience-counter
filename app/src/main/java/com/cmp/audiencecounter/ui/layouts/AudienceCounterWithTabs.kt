package com.cmp.audiencecounter.ui.layouts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.cmp.audiencecounter.R
import com.cmp.audiencecounter.model.AudienceRecord
import com.cmp.audiencecounter.presentation.AudienceCounterAction
import com.cmp.audiencecounter.presentation.AudienceCounterError
import com.cmp.audiencecounter.presentation.AudienceCounterTab
import com.cmp.audiencecounter.presentation.AudienceCounterUiState

@Composable
fun AudienceCounterWithTabs(
    modifier: Modifier = Modifier,
    uiState: AudienceCounterUiState,
    onAction: (AudienceCounterAction) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val persistenceErrorMessage = stringResource(R.string.persistence_error_message)

    val tabs = listOf(
        AudienceCounterTab.DIRECT to stringResource(R.string.direct_count_tab_title),
        AudienceCounterTab.ROWS to stringResource(R.string.row_count_tab_title)
    )

    LaunchedEffect(uiState.error) {
        if (uiState.error == AudienceCounterError.PERSISTENCE) {
            snackbarHostState.showSnackbar(persistenceErrorMessage)
            onAction(AudienceCounterAction.DismissError)
        }
    }

    Box(modifier = modifier) {
        Column {
            PrimaryTabRow(
                selectedTabIndex = tabs.indexOfFirst { it.first == uiState.selectedTab },
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEach { (tab, title) ->
                    Tab(
                        selected = uiState.selectedTab == tab,
                        onClick = { onAction(AudienceCounterAction.SelectTab(tab)) },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (uiState.selectedTab == tab) {
                                    FontWeight.Bold
                                } else {
                                    FontWeight.Normal
                                },
                                fontSize = 18.sp
                            )
                        }
                    )
                }
            }

            when (uiState.selectedTab) {
                AudienceCounterTab.DIRECT -> DirectCounterLayout(
                    uiState = uiState,
                    onAction = onAction
                )

                AudienceCounterTab.ROWS -> RowCounterLayout(
                    uiState = uiState,
                    onAction = onAction
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Preview(showBackground = true, device = Devices.PHONE)
@Composable
fun AudienceCounterWithTabsPreview() {
    AudienceCounterWithTabs(
        uiState = AudienceCounterUiState(
            savedAudiences = listOf(AudienceRecord(1_726_151_700_000, 100))
        ),
        onAction = {}
    )
}

@Preview(showBackground = true, widthDp = 720, heightDp = 360)
@Composable
fun AudienceCounterWithTabsPreviewLandscape() {
    AudienceCounterWithTabs(
        uiState = AudienceCounterUiState(
            savedAudiences = listOf(AudienceRecord(1_726_151_700_000, 100))
        ),
        onAction = {}
    )
}

