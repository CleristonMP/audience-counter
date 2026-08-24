package com.cmp.audiencecounter.presentation

import com.cmp.audiencecounter.model.AudienceRecord

data class AudienceCounterUiState(
    val savedAudiences: List<AudienceRecord> = emptyList(),
    val directCount: Int = 0,
    val rowCount: Int = 0,
    val currentRow: Int = 1,
    val peopleInCurrentRow: Int = 0,
    val completedRowCounts: List<Int> = emptyList(),
    val selectedTab: AudienceCounterTab = AudienceCounterTab.DIRECT,
    val isCountingRows: Boolean = false,
    val isSaving: Boolean = false,
    val error: AudienceCounterError? = null
)

enum class AudienceCounterError {
    PERSISTENCE
}

enum class AudienceCounterTab {
    DIRECT,
    ROWS
}

sealed interface AudienceCounterAction {
    data class SelectTab(val tab: AudienceCounterTab) : AudienceCounterAction
    data class ChangeRowCount(val count: Int) : AudienceCounterAction

    data object IncrementDirectCount : AudienceCounterAction
    data object DecrementDirectCount : AudienceCounterAction
    data object ResetDirectCount : AudienceCounterAction
    data object SaveDirectCount : AudienceCounterAction
    data object ClearHistory : AudienceCounterAction
    data object StartRowCount : AudienceCounterAction
    data object IncrementCurrentRow : AudienceCounterAction
    data object DecrementCurrentRow : AudienceCounterAction
    data object ResetCurrentRow : AudienceCounterAction
    data object CompleteCurrentRow : AudienceCounterAction
    data object SaveRowTotal : AudienceCounterAction
    data object DismissError : AudienceCounterAction
}
