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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.cmp.audiencecounter.R
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Composable
fun AudienceCounterWithTabs(
    modifier: Modifier = Modifier,
    savedAudiences: List<Pair<String, Int>>,
    onAddAudience: suspend (Int) -> Unit,
    onClearAudiences: suspend () -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var isPersisting by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val persistenceErrorMessage = stringResource(R.string.persistence_error_message)

    val tabs = listOf(
        stringResource(R.string.direct_count_tab_title),
        stringResource(R.string.row_count_tab_title)
    )

    fun launchPersistence(
        onSuccess: () -> Unit = {},
        operation: suspend () -> Unit
    ) {
        if (isPersisting) return

        isPersisting = true
        coroutineScope.launch {
            var persistenceFailed = false
            try {
                operation()
                onSuccess()
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (_: Exception) {
                persistenceFailed = true
            } finally {
                isPersisting = false
            }

            if (persistenceFailed) {
                snackbarHostState.showSnackbar(persistenceErrorMessage)
            }
        }
    }

    Box(modifier = modifier) {
        Column {
            PrimaryTabRow(
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
                                fontWeight = if (selectedTabIndex == index) {
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

            when (selectedTabIndex) {
                0 -> DirectCounterLayout(
                    savedAudiences = savedAudiences,
                    isPersisting = isPersisting,
                    onAddAudience = { count, onSuccess ->
                        launchPersistence(onSuccess) { onAddAudience(count) }
                    },
                    onClearAudiences = { launchPersistence(operation = onClearAudiences) }
                )

                1 -> RowCounterLayout(
                    savedAudiences = savedAudiences,
                    isPersisting = isPersisting,
                    onAddAudience = { count, onSuccess ->
                        launchPersistence(onSuccess) { onAddAudience(count) }
                    },
                    onClearAudiences = { launchPersistence(operation = onClearAudiences) }
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
        savedAudiences = listOf("12/09/2024 14:35" to 100, "11/09/2024 15:10" to 80),
        onAddAudience = {},
        onClearAudiences = {}
    )
}

@Preview(showBackground = true, widthDp = 720, heightDp = 360)
@Composable
fun AudienceCounterWithTabsPreviewLandscape() {
    AudienceCounterWithTabs(
        savedAudiences = listOf("12/09/2024 14:35" to 100, "11/09/2024 15:10" to 80),
        onAddAudience = {},
        onClearAudiences = {}
    )
}

