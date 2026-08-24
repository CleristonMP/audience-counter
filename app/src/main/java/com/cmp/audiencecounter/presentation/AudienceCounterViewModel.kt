package com.cmp.audiencecounter.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
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
            directCount = savedStateHandle[DIRECT_COUNT_KEY] ?: 0,
            rowCount = savedStateHandle[ROW_COUNT_KEY] ?: 0,
            currentRow = savedStateHandle[CURRENT_ROW_KEY] ?: 1,
            peopleInCurrentRow = savedStateHandle[PEOPLE_IN_CURRENT_ROW_KEY] ?: 0,
            completedRowCounts = savedStateHandle
                .get<ArrayList<Int>>(COMPLETED_ROW_COUNTS_KEY)
                ?.toList()
                .orEmpty(),
            selectedTabIndex = savedStateHandle[SELECTED_TAB_INDEX_KEY] ?: 0,
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
            is AudienceCounterAction.SelectTab -> selectTab(action.index)
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

    private fun selectTab(index: Int) {
        updateState { it.copy(selectedTabIndex = index) }
    }

    private fun updateDirectCount(change: Int) {
        updateState { state ->
            if (state.isSaving) state else state.copy(
                directCount = (state.directCount + change).coerceAtLeast(0)
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
            if (state.isSaving) state else state.copy(
                rowCount = count.coerceAtLeast(0),
                currentRow = 1,
                peopleInCurrentRow = 0,
                completedRowCounts = emptyList()
            )
        }
    }

    private fun updatePeopleInCurrentRow(change: Int) {
        updateState { state ->
            if (state.isSaving || !state.isCountingRows) state else state.copy(
                peopleInCurrentRow = (state.peopleInCurrentRow + change).coerceAtLeast(0)
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

        val total = state.completedRowCounts.sum()
        launchPersistence(
            operation = { repository.addAudience(total) },
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
        savedStateHandle[SELECTED_TAB_INDEX_KEY] = state.selectedTabIndex
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
        const val SELECTED_TAB_INDEX_KEY = "selected_tab_index"
        const val IS_COUNTING_ROWS_KEY = "is_counting_rows"
    }
}
