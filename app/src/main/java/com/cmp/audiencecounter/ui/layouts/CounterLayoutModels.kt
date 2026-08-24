package com.cmp.audiencecounter.ui.layouts

import com.cmp.audiencecounter.model.AudienceRecord
import com.cmp.audiencecounter.presentation.AudienceCounterUiState

data class DirectCounterLayoutState(
    val savedAudiences: List<AudienceRecord>,
    val count: Int,
    val isSaving: Boolean
)

data class DirectCounterEvents(
    val onReset: () -> Unit,
    val onDecrement: () -> Unit,
    val onIncrement: () -> Unit,
    val onSave: () -> Unit,
    val onClearHistory: () -> Unit
)

data class RowCounterLayoutState(
    val savedAudiences: List<AudienceRecord>,
    val isSaving: Boolean,
    val isCounting: Boolean,
    val rowCount: Int,
    val currentRow: Int,
    val peopleInCurrentRow: Int,
    val completedRowCounts: List<Int>
) {
    val hasActiveRow: Boolean
        get() = isCounting && rowCount > 0 && currentRow <= rowCount

    val isComplete: Boolean
        get() = rowCount > 0 && completedRowCounts.size == rowCount

    val totalPeople: Long
        get() = completedRowCounts.sumOf { it.toLong() }

    val canSaveTotal: Boolean
        get() = isComplete && currentRow > rowCount && !isSaving
}

data class RowCounterEvents(
    val onSaveTotal: () -> Unit,
    val onClearHistory: () -> Unit,
    val onStart: () -> Unit,
    val onChangeRowCount: (Int) -> Unit,
    val onCompleteCurrentRow: () -> Unit,
    val onResetCurrentRow: () -> Unit,
    val onDecrementCurrentRow: () -> Unit,
    val onIncrementCurrentRow: () -> Unit
)

fun AudienceCounterUiState.toDirectLayoutState() = DirectCounterLayoutState(
    savedAudiences = savedAudiences,
    count = directCount,
    isSaving = isSaving
)

fun AudienceCounterUiState.toRowLayoutState() = RowCounterLayoutState(
    savedAudiences = savedAudiences,
    isSaving = isSaving,
    isCounting = isCountingRows,
    rowCount = rowCount,
    currentRow = currentRow,
    peopleInCurrentRow = peopleInCurrentRow,
    completedRowCounts = completedRowCounts
)
