package com.cmp.audiencecounter.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cmp.audiencecounter.repository.AudienceRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AudienceCounterViewModel(
    private val repository: AudienceRepository
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(AudienceCounterUiState())
    val uiState: StateFlow<AudienceCounterUiState> = mutableUiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.audiences.collect { savedAudiences ->
                mutableUiState.update { it.copy(savedAudiences = savedAudiences) }
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
        mutableUiState.update { it.copy(selectedTabIndex = index) }
    }

    private fun updateDirectCount(change: Int) {
        mutableUiState.update { state ->
            if (state.isSaving) state else state.copy(
                directCount = (state.directCount + change).coerceAtLeast(0)
            )
        }
    }

    private fun resetDirectCount() {
        mutableUiState.update { state ->
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
        mutableUiState.update { state ->
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
        mutableUiState.update { state ->
            if (state.isSaving) state else state.copy(
                rowCount = count.coerceAtLeast(0),
                currentRow = 1,
                peopleInCurrentRow = 0,
                completedRowCounts = emptyList()
            )
        }
    }

    private fun updatePeopleInCurrentRow(change: Int) {
        mutableUiState.update { state ->
            if (state.isSaving || !state.isCountingRows) state else state.copy(
                peopleInCurrentRow = (state.peopleInCurrentRow + change).coerceAtLeast(0)
            )
        }
    }

    private fun resetCurrentRow() {
        mutableUiState.update { state ->
            if (state.isSaving) state else state.copy(peopleInCurrentRow = 0)
        }
    }

    private fun completeCurrentRow() {
        mutableUiState.update { state ->
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
        mutableUiState.update { it.copy(error = null) }
    }

    private fun launchPersistence(
        operation: suspend () -> Unit,
        onSuccess: (AudienceCounterUiState) -> AudienceCounterUiState = { it }
    ) {
        if (mutableUiState.value.isSaving) return

        mutableUiState.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            try {
                operation()
                mutableUiState.update { onSuccess(it).copy(isSaving = false) }
            } catch (cancellationException: CancellationException) {
                mutableUiState.update { it.copy(isSaving = false) }
                throw cancellationException
            } catch (_: Exception) {
                mutableUiState.update {
                    it.copy(isSaving = false, error = AudienceCounterError.PERSISTENCE)
                }
            }
        }
    }

    class Factory(
        private val repository: AudienceRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(AudienceCounterViewModel::class.java))
            return AudienceCounterViewModel(repository) as T
        }
    }
}
