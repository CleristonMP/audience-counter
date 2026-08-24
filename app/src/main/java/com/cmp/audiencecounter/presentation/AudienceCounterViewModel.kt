package com.cmp.audiencecounter.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.cmp.audiencecounter.model.MAX_AUDIENCE_COUNT
import com.cmp.audiencecounter.model.MAX_ROW_COUNT
import com.cmp.audiencecounter.repository.AudienceRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AudienceCounterViewModel(
    private val repository: AudienceRepository,
    private val savedStateHandle: SavedStateHandle = SavedStateHandle()
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(
        AudienceCounterUiState(
            directCount = savedCount(DIRECT_COUNT_KEY),
            rowCount = savedCount(ROW_COUNT_KEY, maximum = MAX_ROW_COUNT),
            currentRow = savedCount(
                CURRENT_ROW_KEY,
                default = 1,
                maximum = MAX_ROW_COUNT + 1
            ),
            peopleInCurrentRow = savedCount(PEOPLE_IN_CURRENT_ROW_KEY),
            completedRowCounts = savedRowCounts(),
            selectedTab = savedTab(),
            isCountingRows = savedStateHandle[IS_COUNTING_ROWS_KEY] ?: false
        )
    )
    val uiState: StateFlow<AudienceCounterUiState> = mutableUiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.audiences.collect { savedAudiences ->
                updateState { it.copy(savedAudiences = savedAudiences) }
            }
        }
    }

    fun onAction(action: AudienceCounterAction) {
        when (action) {
            is AudienceCounterAction.SelectTab -> selectTab(action.tab)
            is AudienceCounterAction.ChangeRowCount -> changeRowCount(action.count)
            AudienceCounterAction.IncrementDirectCount -> updateDirectCount(1)
            AudienceCounterAction.DecrementDirectCount -> updateDirectCount(-1)
            AudienceCounterAction.ResetDirectCount -> resetDirectCount()
            AudienceCounterAction.SaveDirectCount -> saveDirectCount()
            AudienceCounterAction.ClearHistory -> clearHistory()
            AudienceCounterAction.StartRowCount -> startRowCount()
            AudienceCounterAction.IncrementCurrentRow -> updatePeopleInCurrentRow(1)
            AudienceCounterAction.DecrementCurrentRow -> updatePeopleInCurrentRow(-1)
            AudienceCounterAction.ResetCurrentRow -> resetCurrentRow()
            AudienceCounterAction.CompleteCurrentRow -> completeCurrentRow()
            AudienceCounterAction.SaveRowTotal -> saveRowTotal()
            AudienceCounterAction.DismissError -> dismissError()
        }
    }

    private fun selectTab(tab: AudienceCounterTab) {
        updateState { it.copy(selectedTab = tab) }
    }

    private fun updateDirectCount(change: Int) {
        updateState { state ->
            if (state.isSaving) state else state.copy(
                directCount = state.directCount.safeChange(change)
            )
        }
    }

    private fun resetDirectCount() {
        updateState { state ->
            if (state.isSaving) state else state.copy(directCount = 0)
        }
    }

    private fun saveDirectCount() {
        val count = mutableUiState.value.directCount
        if (count <= 0) return

        launchPersistence(
            operation = { repository.addAudience(count) },
            onSuccess = { it.copy(directCount = 0) }
        )
    }

    private fun clearHistory() {
        launchPersistence(operation = repository::clearAudiences)
    }

    private fun startRowCount() {
        updateState { state ->
            if (state.isSaving) state else state.copy(
                rowCount = 0,
                currentRow = 1,
                peopleInCurrentRow = 0,
                completedRowCounts = emptyList(),
                isCountingRows = true
            )
        }
    }

    private fun changeRowCount(count: Int) {
        updateState { state ->
            if (state.isSaving || count !in 0..MAX_ROW_COUNT) state else state.copy(
                rowCount = count,
                currentRow = 1,
                peopleInCurrentRow = 0,
                completedRowCounts = emptyList()
            )
        }
    }

    private fun updatePeopleInCurrentRow(change: Int) {
        updateState { state ->
            if (state.isSaving || !state.isCountingRows) state else state.copy(
                peopleInCurrentRow = state.peopleInCurrentRow.safeChange(change)
            )
        }
    }

    private fun resetCurrentRow() {
        updateState { state ->
            if (state.isSaving) state else state.copy(peopleInCurrentRow = 0)
        }
    }

    private fun completeCurrentRow() {
        updateState { state ->
            if (state.isSaving || state.rowCount <= 0 || state.currentRow > state.rowCount) {
                state
            } else {
                state.copy(
                    currentRow = state.currentRow + 1,
                    peopleInCurrentRow = 0,
                    completedRowCounts = state.completedRowCounts + state.peopleInCurrentRow,
                    isCountingRows = state.currentRow < state.rowCount
                )
            }
        }
    }

    private fun saveRowTotal() {
        val state = mutableUiState.value
        if (state.rowCount <= 0 || state.completedRowCounts.size != state.rowCount) return

        val total = state.completedRowCounts.sumOf { it.toLong() }
        if (total !in 1..MAX_AUDIENCE_COUNT.toLong()) return
        launchPersistence(
            operation = { repository.addAudience(total.toInt()) },
            onSuccess = {
                it.copy(
                    rowCount = 0,
                    currentRow = 1,
                    peopleInCurrentRow = 0,
                    completedRowCounts = emptyList(),
                    isCountingRows = false
                )
            }
        )
    }

    private fun dismissError() {
        updateState { it.copy(error = null) }
    }

    private fun launchPersistence(
        operation: suspend () -> Unit,
        onSuccess: (AudienceCounterUiState) -> AudienceCounterUiState = { it }
    ) {
        if (mutableUiState.value.isSaving) return

        updateState { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            try {
                operation()
                updateState { onSuccess(it).copy(isSaving = false) }
            } catch (cancellationException: CancellationException) {
                updateState { it.copy(isSaving = false) }
                throw cancellationException
            } catch (_: Exception) {
                updateState {
                    it.copy(isSaving = false, error = AudienceCounterError.PERSISTENCE)
                }
            }
        }
    }

    private fun updateState(
        transform: (AudienceCounterUiState) -> AudienceCounterUiState
    ) {
        mutableUiState.update { currentState ->
            transform(currentState).also(::saveRestorableState)
        }
    }

    private fun saveRestorableState(state: AudienceCounterUiState) {
        savedStateHandle[DIRECT_COUNT_KEY] = state.directCount
        savedStateHandle[ROW_COUNT_KEY] = state.rowCount
        savedStateHandle[CURRENT_ROW_KEY] = state.currentRow
        savedStateHandle[PEOPLE_IN_CURRENT_ROW_KEY] = state.peopleInCurrentRow
        savedStateHandle[COMPLETED_ROW_COUNTS_KEY] = ArrayList(state.completedRowCounts)
        savedStateHandle[SELECTED_TAB_KEY] = state.selectedTab.name
        savedStateHandle[IS_COUNTING_ROWS_KEY] = state.isCountingRows
    }

    class Factory(
        private val repository: AudienceRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(AudienceCounterViewModel::class.java))
            return AudienceCounterViewModel(repository, SavedStateHandle()) as T
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            require(modelClass.isAssignableFrom(AudienceCounterViewModel::class.java))
            return AudienceCounterViewModel(repository, extras.createSavedStateHandle()) as T
        }
    }

    private companion object {
        const val DIRECT_COUNT_KEY = "direct_count"
        const val ROW_COUNT_KEY = "row_count"
        const val CURRENT_ROW_KEY = "current_row"
        const val PEOPLE_IN_CURRENT_ROW_KEY = "people_in_current_row"
        const val COMPLETED_ROW_COUNTS_KEY = "completed_row_counts"
        const val SELECTED_TAB_KEY = "selected_tab"
        const val LEGACY_SELECTED_TAB_INDEX_KEY = "selected_tab_index"
        const val IS_COUNTING_ROWS_KEY = "is_counting_rows"
    }

    private fun savedCount(
        key: String,
        default: Int = 0,
        maximum: Int = MAX_AUDIENCE_COUNT
    ): Int = (savedStateHandle.get<Any>(key) as? Int)
        ?.takeIf { it in 0..maximum }
        ?: default

    private fun savedRowCounts(): List<Int> {
        val values = savedStateHandle.get<Any>(COMPLETED_ROW_COUNTS_KEY) as? List<*>
            ?: return emptyList()
        return values.map { value ->
            (value as? Int)?.takeIf { it in 0..MAX_AUDIENCE_COUNT }
                ?: return emptyList()
        }
    }

    private fun savedTab(): AudienceCounterTab {
        val tabName = savedStateHandle.get<Any>(SELECTED_TAB_KEY) as? String
        AudienceCounterTab.entries.firstOrNull { it.name == tabName }?.let { return it }

        return when (savedStateHandle.get<Any>(LEGACY_SELECTED_TAB_INDEX_KEY) as? Int) {
            1 -> AudienceCounterTab.ROWS
            else -> AudienceCounterTab.DIRECT
        }
    }

    private fun Int.safeChange(change: Int): Int = when {
        change > 0 && this < MAX_AUDIENCE_COUNT -> this + 1
        change < 0 && this > 0 -> this - 1
        else -> this
    }
}
