package com.cmp.audiencecounter.ui.layouts

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CounterLayoutModelsTest {
    @Test
    fun activeRowIsDerivedFromCountingProgress() {
        val state = rowState(
            isCounting = true,
            rowCount = 3,
            currentRow = 2
        )

        assertTrue(state.hasActiveRow)
        assertFalse(state.isComplete)
    }

    @Test
    fun completedRowsExposeSafeTotalAndSaveAvailability() {
        val state = rowState(
            rowCount = 3,
            currentRow = 4,
            completedRowCounts = listOf(10, 20, 30)
        )

        assertTrue(state.isComplete)
        assertEquals(60L, state.totalPeople)
        assertTrue(state.canSaveTotal)
    }

    @Test
    fun savingDisablesCompletedTotalAction() {
        val state = rowState(
            isSaving = true,
            rowCount = 1,
            currentRow = 2,
            completedRowCounts = listOf(10)
        )

        assertFalse(state.canSaveTotal)
    }

    private fun rowState(
        isSaving: Boolean = false,
        isCounting: Boolean = false,
        rowCount: Int = 0,
        currentRow: Int = 1,
        completedRowCounts: List<Int> = emptyList()
    ) = RowCounterLayoutState(
        savedAudiences = emptyList(),
        isSaving = isSaving,
        isCounting = isCounting,
        rowCount = rowCount,
        currentRow = currentRow,
        peopleInCurrentRow = 0,
        completedRowCounts = completedRowCounts
    )
}
