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

    BoxWithConstraints {
        if (maxWidth > maxHeight) {
            LandscapeDirectCounterLayout(
                savedAudiences = uiState.savedAudiences,
                audience = uiState.directCount,
                showDialog = showDialog,
                isSaving = uiState.isSaving,
                onResetCounting = { onAction(AudienceCounterAction.ResetDirectCount) },
                onDecrement = { onAction(AudienceCounterAction.DecrementDirectCount) },
                onIncrement = { onAction(AudienceCounterAction.IncrementDirectCount) },
                onSave = { onAction(AudienceCounterAction.SaveDirectCount) },
                onClearAudiences = { onAction(AudienceCounterAction.ClearHistory) },
                onShowingDialog = { value -> showDialog = value }
            )
        } else {
            PortraitDirectCounterLayout(
                savedAudiences = uiState.savedAudiences,
                audience = uiState.directCount,
                showDialog = showDialog,
                isSaving = uiState.isSaving,
                onResetCounting = { onAction(AudienceCounterAction.ResetDirectCount) },
                onDecrement = { onAction(AudienceCounterAction.DecrementDirectCount) },
                onIncrement = { onAction(AudienceCounterAction.IncrementDirectCount) },
                onSave = { onAction(AudienceCounterAction.SaveDirectCount) },
                onClearAudiences = { onAction(AudienceCounterAction.ClearHistory) },
                onShowingDialog = { value -> showDialog = value }
            )
        }
    }
}
