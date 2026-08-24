package com.cmp.audiencecounter.ui.layouts

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import com.cmp.audiencecounter.presentation.AudienceCounterAction
import com.cmp.audiencecounter.presentation.AudienceCounterUiState
import com.cmp.audiencecounter.ui.layouts.landscape.LandscapeDirectCounterLayout
import com.cmp.audiencecounter.ui.layouts.portrait.PortraitDirectCounterLayout

@Composable
fun DirectCounterLayout(
    uiState: AudienceCounterUiState,
    onAction: (AudienceCounterAction) -> Unit
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    val layoutState = uiState.toDirectLayoutState()
    val events = DirectCounterEvents(
        onReset = { onAction(AudienceCounterAction.ResetDirectCount) },
        onDecrement = { onAction(AudienceCounterAction.DecrementDirectCount) },
        onIncrement = { onAction(AudienceCounterAction.IncrementDirectCount) },
        onSave = { onAction(AudienceCounterAction.SaveDirectCount) },
        onClearHistory = { onAction(AudienceCounterAction.ClearHistory) }
    )

    BoxWithConstraints {
        if (maxWidth > maxHeight) {
            LandscapeDirectCounterLayout(
                state = layoutState,
                events = events,
                showClearConfirmation = showDialog,
                onClearConfirmationChange = { showDialog = it }
            )
        } else {
            PortraitDirectCounterLayout(
                state = layoutState,
                events = events,
                showClearConfirmation = showDialog,
                onClearConfirmationChange = { showDialog = it }
            )
        }
    }
}
