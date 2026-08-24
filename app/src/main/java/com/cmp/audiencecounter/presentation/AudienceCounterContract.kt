package com.cmp.audiencecounter.presentation

data class AudienceCounterUiState(
    val savedAudiences: List<Pair<String, Int>> = emptyList(),
    val directCount: Int = 0,
    val rowCount: Int = 0,
    val currentRow: Int = 1,
    val peopleInCurrentRow: Int = 0,
    val completedRowCounts: List<Int> = emptyList(),
    val selectedTabIndex: Int = 0,
    val isCountingRows: Boolean = false,
    val isSaving: Boolean = false,
    val error: AudienceCounterError? = null
)

enum class AudienceCounterError {
    PERSISTENCE
}

sealed interface AudienceCounterAction {
    data class SelectTab(val index: Int) : AudienceCounterAction
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
