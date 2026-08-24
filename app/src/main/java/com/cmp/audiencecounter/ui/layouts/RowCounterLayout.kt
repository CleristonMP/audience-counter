package com.cmp.audiencecounter.ui.layouts

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.cmp.audiencecounter.presentation.AudienceCounterAction
import com.cmp.audiencecounter.presentation.AudienceCounterUiState
import com.cmp.audiencecounter.ui.layouts.landscape.LandscapeRowCounterLayout
import com.cmp.audiencecounter.ui.layouts.portrait.PortraitRowCounterLayout

@Composable
fun RowCounterLayout(
    uiState: AudienceCounterUiState,
    onAction: (AudienceCounterAction) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    BoxWithConstraints {
        if (maxWidth > maxHeight) {
            LandscapeRowCounterLayout(
                showDialog = showDialog,
                isCounting = uiState.isCountingRows,
                rowCount = uiState.rowCount,
                currentRow = uiState.currentRow,
                peopleInRow = uiState.peopleInCurrentRow,
                rowCounts = uiState.completedRowCounts,
                savedAudiences = uiState.savedAudiences,
                isSaving = uiState.isSaving,
                onSaveTotal = { onAction(AudienceCounterAction.SaveRowTotal) },
                onClearAudiences = { onAction(AudienceCounterAction.ClearHistory) },
                onStartCounting = { onAction(AudienceCounterAction.StartRowCount) },
                onChangeRowCount = {
                    onAction(AudienceCounterAction.ChangeRowCount(it))
                },
                onCompleteCurrentRow = {
                    onAction(AudienceCounterAction.CompleteCurrentRow)
                },
                onResetCurrentRow = { onAction(AudienceCounterAction.ResetCurrentRow) },
                onDecrementCurrentRow = {
                    onAction(AudienceCounterAction.DecrementCurrentRow)
                },
                onIncrementCurrentRow = {
                    onAction(AudienceCounterAction.IncrementCurrentRow)
                },
                onShowingDialog = { showDialog = it }
            )
        } else {
            PortraitRowCounterLayout(
                showDialog = showDialog,
                isCounting = uiState.isCountingRows,
                rowCount = uiState.rowCount,
                currentRow = uiState.currentRow,
                peopleInRow = uiState.peopleInCurrentRow,
                rowCounts = uiState.completedRowCounts,
                savedAudiences = uiState.savedAudiences,
                isSaving = uiState.isSaving,
                onSaveTotal = { onAction(AudienceCounterAction.SaveRowTotal) },
                onClearAudiences = { onAction(AudienceCounterAction.ClearHistory) },
                onStartCounting = { onAction(AudienceCounterAction.StartRowCount) },
                onChangeRowCount = {
                    onAction(AudienceCounterAction.ChangeRowCount(it))
                },
                onCompleteCurrentRow = {
                    onAction(AudienceCounterAction.CompleteCurrentRow)
                },
                onResetCurrentRow = { onAction(AudienceCounterAction.ResetCurrentRow) },
                onDecrementCurrentRow = {
                    onAction(AudienceCounterAction.DecrementCurrentRow)
                },
                onIncrementCurrentRow = {
                    onAction(AudienceCounterAction.IncrementCurrentRow)
                },
                onShowingDialog = { showDialog = it }
            )
        }
    }
}
