package com.cmp.audiencecounter.ui.layouts.landscape

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmp.audiencecounter.model.AudienceRecord
import com.cmp.audiencecounter.ui.components.AudienceHistorySection
import com.cmp.audiencecounter.ui.components.CurrentRowActions
import com.cmp.audiencecounter.ui.components.CurrentRowHeader
import com.cmp.audiencecounter.ui.components.Footer
import com.cmp.audiencecounter.ui.components.RowCountSetup
import com.cmp.audiencecounter.ui.components.RowTotalSummary
import com.cmp.audiencecounter.ui.layouts.RowCounterEvents
import com.cmp.audiencecounter.ui.layouts.RowCounterLayoutState

@Composable
fun LandscapeRowCounterLayout(
    state: RowCounterLayoutState,
    events: RowCounterEvents,
    showClearConfirmation: Boolean,
    onClearConfirmationChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AudienceHistorySection(
                    savedAudiences = state.savedAudiences,
                    isSaving = state.isSaving,
                    showClearConfirmation = showClearConfirmation,
                    onClearConfirmationChange = onClearConfirmationChange,
                    onClearHistory = events.onClearHistory
                )
                Spacer(modifier = Modifier.height(16.dp))
                RowCountSetup(
                    isCounting = state.isCounting,
                    rowCount = state.rowCount,
                    isSaving = state.isSaving,
                    onStart = events.onStart,
                    onRowCountChange = events.onChangeRowCount
                )
            }
            if (state.hasActiveRow) {
                Spacer(modifier = Modifier.width(16.dp))
                CurrentRowHeader(
                    currentRow = state.currentRow,
                    rowCount = state.rowCount,
                    peopleInCurrentRow = state.peopleInCurrentRow,
                    onCompleteCurrentRow = events.onCompleteCurrentRow,
                    modifier = Modifier.padding(16.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                CurrentRowActions(
                    completedRowCounts = state.completedRowCounts,
                    onReset = events.onResetCurrentRow,
                    onDecrement = events.onDecrementCurrentRow,
                    onIncrement = events.onIncrementCurrentRow,
                    spacing = 16.dp,
                    modifier = Modifier.padding(16.dp)
                )
            }
            if (state.isComplete) {
                Spacer(modifier = Modifier.width(48.dp))
                RowTotalSummary(
                    totalPeople = state.totalPeople,
                    canSave = state.canSaveTotal,
                    onSave = events.onSaveTotal,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Footer(fontSize = 8.sp)
    }
}

@Preview(showBackground = true, widthDp = 720, heightDp = 360)
@Composable
private fun LandscapeRowCounterLayoutPreview() {
    LandscapeRowCounterLayout(
        state = RowCounterLayoutState(
            savedAudiences = listOf(AudienceRecord(1_726_151_700_000, 100)),
            isSaving = false,
            isCounting = false,
            rowCount = 5,
            currentRow = 6,
            peopleInCurrentRow = 0,
            completedRowCounts = listOf(10, 20, 30, 40, 50)
        ),
        events = RowCounterEvents(
            onSaveTotal = {},
            onClearHistory = {},
            onStart = {},
            onChangeRowCount = {},
            onCompleteCurrentRow = {},
            onResetCurrentRow = {},
            onDecrementCurrentRow = {},
            onIncrementCurrentRow = {}
        ),
        showClearConfirmation = false,
        onClearConfirmationChange = {}
    )
}
