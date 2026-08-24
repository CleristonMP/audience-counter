package com.cmp.audiencecounter.presentation

import androidx.lifecycle.SavedStateHandle
import com.cmp.audiencecounter.repository.AudienceRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class AudienceCounterViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun repositoryHistoryIsExposedInUiState() = runTest(mainDispatcherRule.dispatcher) {
        val history = listOf("24/08/2026 10:15" to 25)
        val viewModel = AudienceCounterViewModel(FakeAudienceRepository(history))

        runCurrent()

        assertEquals(history, viewModel.uiState.value.savedAudiences)
    }

    @Test
    fun successfulDirectSaveResetsCountAfterRepositoryCompletes() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository = FakeAudienceRepository()
            val viewModel = AudienceCounterViewModel(repository)
            runCurrent()
            viewModel.onAction(AudienceCounterAction.IncrementDirectCount)
            viewModel.onAction(AudienceCounterAction.IncrementDirectCount)

            viewModel.onAction(AudienceCounterAction.SaveDirectCount)

            assertTrue(viewModel.uiState.value.isSaving)
            assertEquals(2, viewModel.uiState.value.directCount)
            runCurrent()
            assertEquals(listOf(2), repository.addedCounts)
            assertEquals(0, viewModel.uiState.value.directCount)
            assertFalse(viewModel.uiState.value.isSaving)
        }

    @Test
    fun failedDirectSavePreservesCountAndExposesError() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository = FakeAudienceRepository().apply {
                addFailure = IOException("Write failed")
            }
            val viewModel = AudienceCounterViewModel(repository)
            runCurrent()
            viewModel.onAction(AudienceCounterAction.IncrementDirectCount)

            viewModel.onAction(AudienceCounterAction.SaveDirectCount)
            runCurrent()

            assertEquals(1, viewModel.uiState.value.directCount)
            assertFalse(viewModel.uiState.value.isSaving)
            assertEquals(AudienceCounterError.PERSISTENCE, viewModel.uiState.value.error)

            viewModel.onAction(AudienceCounterAction.DismissError)
            assertNull(viewModel.uiState.value.error)
        }

    @Test
    fun secondSaveIsIgnoredWhileFirstOperationIsRunning() =
        runTest(mainDispatcherRule.dispatcher) {
            val saveGate = CompletableDeferred<Unit>()
            val repository = FakeAudienceRepository().apply { addGate = saveGate }
            val viewModel = AudienceCounterViewModel(repository)
            runCurrent()
            viewModel.onAction(AudienceCounterAction.IncrementDirectCount)

            viewModel.onAction(AudienceCounterAction.SaveDirectCount)
            viewModel.onAction(AudienceCounterAction.SaveDirectCount)
            runCurrent()

            assertEquals(1, repository.addCalls)
            assertTrue(viewModel.uiState.value.isSaving)

            saveGate.complete(Unit)
            advanceUntilIdle()

            assertEquals(listOf(1), repository.addedCounts)
            assertFalse(viewModel.uiState.value.isSaving)
        }

    @Test
    fun completedRowsAreSummedAndResetOnlyAfterSuccessfulSave() =
        runTest(mainDispatcherRule.dispatcher) {
            val repository = FakeAudienceRepository()
            val viewModel = AudienceCounterViewModel(repository)
            runCurrent()

            viewModel.onAction(AudienceCounterAction.StartRowCount)
            viewModel.onAction(AudienceCounterAction.ChangeRowCount(2))
            viewModel.onAction(AudienceCounterAction.IncrementCurrentRow)
            viewModel.onAction(AudienceCounterAction.CompleteCurrentRow)
            viewModel.onAction(AudienceCounterAction.IncrementCurrentRow)
            viewModel.onAction(AudienceCounterAction.IncrementCurrentRow)
            viewModel.onAction(AudienceCounterAction.CompleteCurrentRow)
            viewModel.onAction(AudienceCounterAction.SaveRowTotal)
            runCurrent()

            assertEquals(listOf(3), repository.addedCounts)
            assertEquals(0, viewModel.uiState.value.rowCount)
            assertEquals(emptyList<Int>(), viewModel.uiState.value.completedRowCounts)
            assertFalse(viewModel.uiState.value.isCountingRows)
        }

    @Test
    fun clearHistoryUsesRepositoryAndTracksOperationState() =
        runTest(mainDispatcherRule.dispatcher) {
            val clearGate = CompletableDeferred<Unit>()
            val repository = FakeAudienceRepository().apply { this.clearGate = clearGate }
            val viewModel = AudienceCounterViewModel(repository)
            runCurrent()

            viewModel.onAction(AudienceCounterAction.ClearHistory)
            runCurrent()

            assertTrue(viewModel.uiState.value.isSaving)
            assertEquals(1, repository.clearCalls)

            clearGate.complete(Unit)
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isSaving)
        }

    @Test
    fun operationalStateIsRestoredFromSavedStateHandle() =
        runTest(mainDispatcherRule.dispatcher) {
            val savedStateHandle = SavedStateHandle()
            val repository = FakeAudienceRepository()
            val viewModel = AudienceCounterViewModel(repository, savedStateHandle)
            runCurrent()

            viewModel.onAction(AudienceCounterAction.IncrementDirectCount)
            viewModel.onAction(AudienceCounterAction.IncrementDirectCount)
            viewModel.onAction(AudienceCounterAction.SelectTab(1))
            viewModel.onAction(AudienceCounterAction.StartRowCount)
            viewModel.onAction(AudienceCounterAction.ChangeRowCount(2))
            viewModel.onAction(AudienceCounterAction.IncrementCurrentRow)
            viewModel.onAction(AudienceCounterAction.CompleteCurrentRow)
            viewModel.onAction(AudienceCounterAction.IncrementCurrentRow)

            val restoredViewModel = AudienceCounterViewModel(repository, savedStateHandle)
            runCurrent()
            val restoredState = restoredViewModel.uiState.value

            assertEquals(2, restoredState.directCount)
            assertEquals(1, restoredState.selectedTabIndex)
            assertEquals(2, restoredState.rowCount)
            assertEquals(2, restoredState.currentRow)
            assertEquals(1, restoredState.peopleInCurrentRow)
            assertEquals(listOf(1), restoredState.completedRowCounts)
            assertTrue(restoredState.isCountingRows)
        }

    @Test
    fun transientErrorAndSavingStateAreNotRestored() =
        runTest(mainDispatcherRule.dispatcher) {
            val savedStateHandle = SavedStateHandle()
            val repository = FakeAudienceRepository().apply {
                addFailure = IOException("Write failed")
            }
            val viewModel = AudienceCounterViewModel(repository, savedStateHandle)
            runCurrent()
            viewModel.onAction(AudienceCounterAction.IncrementDirectCount)
            viewModel.onAction(AudienceCounterAction.SaveDirectCount)
            runCurrent()
            assertEquals(AudienceCounterError.PERSISTENCE, viewModel.uiState.value.error)

            val restoredViewModel = AudienceCounterViewModel(repository, savedStateHandle)
            runCurrent()

            assertEquals(1, restoredViewModel.uiState.value.directCount)
            assertFalse(restoredViewModel.uiState.value.isSaving)
            assertNull(restoredViewModel.uiState.value.error)
        }
}

private class FakeAudienceRepository(
    initialAudiences: List<Pair<String, Int>> = emptyList()
) : AudienceRepository {
    private val audienceState = MutableStateFlow(initialAudiences)
    override val audiences: Flow<List<Pair<String, Int>>> = audienceState

    val addedCounts = mutableListOf<Int>()
    var addCalls = 0
    var clearCalls = 0
    var addFailure: Throwable? = null
    var addGate: CompletableDeferred<Unit>? = null
    var clearGate: CompletableDeferred<Unit>? = null

    override suspend fun addAudience(count: Int) {
        addCalls++
        addGate?.await()
        addFailure?.let { throw it }
        addedCounts += count
    }

    override suspend fun clearAudiences() {
        clearCalls++
        clearGate?.await()
        audienceState.value = emptyList()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val dispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        kotlinx.coroutines.Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        kotlinx.coroutines.Dispatchers.resetMain()
    }
}
