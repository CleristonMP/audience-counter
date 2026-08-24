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
import com.cmp.audiencecounter.presentation.AudienceCounterAction
import com.cmp.audiencecounter.presentation.AudienceCounterError
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
        stringResource(R.string.direct_count_tab_title),
        stringResource(R.string.row_count_tab_title)
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
                selectedTabIndex = uiState.selectedTabIndex,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = uiState.selectedTabIndex == index,
                        onClick = { onAction(AudienceCounterAction.SelectTab(index)) },
                        text = {
                            Text(
                                text = tab,
                                fontWeight = if (uiState.selectedTabIndex == index) {
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

            when (uiState.selectedTabIndex) {
                0 -> DirectCounterLayout(
                    uiState = uiState,
                    onAction = onAction
                )

                1 -> RowCounterLayout(
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
            savedAudiences = listOf("12/09/2024 14:35" to 100, "11/09/2024 15:10" to 80)
        ),
        onAction = {}
    )
}

@Preview(showBackground = true, widthDp = 720, heightDp = 360)
@Composable
fun AudienceCounterWithTabsPreviewLandscape() {
    AudienceCounterWithTabs(
        uiState = AudienceCounterUiState(
            savedAudiences = listOf("12/09/2024 14:35" to 100, "11/09/2024 15:10" to 80)
        ),
        onAction = {}
    )
}

