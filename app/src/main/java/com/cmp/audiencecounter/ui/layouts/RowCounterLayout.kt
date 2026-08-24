package com.cmp.audiencecounter.ui.layouts

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import com.cmp.audiencecounter.presentation.AudienceCounterAction
import com.cmp.audiencecounter.presentation.AudienceCounterUiState
import com.cmp.audiencecounter.ui.layouts.landscape.LandscapeRowCounterLayout
import com.cmp.audiencecounter.ui.layouts.portrait.PortraitRowCounterLayout

@Composable
fun RowCounterLayout(
    uiState: AudienceCounterUiState,
    onAction: (AudienceCounterAction) -> Unit
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    val layoutState = uiState.toRowLayoutState()
    val events = RowCounterEvents(
        onSaveTotal = { onAction(AudienceCounterAction.SaveRowTotal) },
        onClearHistory = { onAction(AudienceCounterAction.ClearHistory) },
        onStart = { onAction(AudienceCounterAction.StartRowCount) },
        onChangeRowCount = { onAction(AudienceCounterAction.ChangeRowCount(it)) },
        onCompleteCurrentRow = { onAction(AudienceCounterAction.CompleteCurrentRow) },
        onResetCurrentRow = { onAction(AudienceCounterAction.ResetCurrentRow) },
        onDecrementCurrentRow = { onAction(AudienceCounterAction.DecrementCurrentRow) },
        onIncrementCurrentRow = { onAction(AudienceCounterAction.IncrementCurrentRow) }
    )

    BoxWithConstraints {
        if (maxWidth > maxHeight) {
            LandscapeRowCounterLayout(
                state = layoutState,
                events = events,
                showClearConfirmation = showDialog,
                onClearConfirmationChange = { showDialog = it }
            )
        } else {
            PortraitRowCounterLayout(
                state = layoutState,
                events = events,
                showClearConfirmation = showDialog,
                onClearConfirmationChange = { showDialog = it }
            )
        }
    }
}
