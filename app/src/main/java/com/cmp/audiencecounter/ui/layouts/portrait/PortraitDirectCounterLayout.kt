package com.cmp.audiencecounter.ui.layouts.portrait

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmp.audiencecounter.model.AudienceRecord
import com.cmp.audiencecounter.ui.components.AudienceHistorySection
import com.cmp.audiencecounter.ui.components.CounterAdjustmentControls
import com.cmp.audiencecounter.ui.components.CounterDisplay
import com.cmp.audiencecounter.ui.components.DirectCounterActions
import com.cmp.audiencecounter.ui.components.Footer
import com.cmp.audiencecounter.ui.layouts.DirectCounterEvents
import com.cmp.audiencecounter.ui.layouts.DirectCounterLayoutState

@Composable
fun PortraitDirectCounterLayout(
    state: DirectCounterLayoutState,
    events: DirectCounterEvents,
    showClearConfirmation: Boolean,
    onClearConfirmationChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AudienceHistorySection(
            savedAudiences = state.savedAudiences,
            isSaving = state.isSaving,
            showClearConfirmation = showClearConfirmation,
            onClearConfirmationChange = onClearConfirmationChange,
            onClearHistory = events.onClearHistory
        )
        Spacer(modifier = Modifier.height(72.dp))
        CounterDisplay(state.count)
        Spacer(modifier = Modifier.height(72.dp))
        DirectCounterActions(
            count = state.count,
            isSaving = state.isSaving,
            onReset = events.onReset,
            onSave = events.onSave
        )
        Spacer(modifier = Modifier.height(68.dp))
        CounterAdjustmentControls(
            onDecrement = events.onDecrement,
            onIncrement = events.onIncrement,
            incrementButtonSize = 120.dp,
            spacing = 64.dp,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.weight(1f))
        Footer(fontSize = 12.sp)
    }
}

@Preview(showBackground = true, device = Devices.PHONE)
@Composable
private fun PortraitDirectCounterLayoutPreview() {
    PortraitDirectCounterLayout(
        state = DirectCounterLayoutState(
            savedAudiences = listOf(AudienceRecord(1_726_151_700_000, 100)),
            count = 5,
            isSaving = false
        ),
        events = DirectCounterEvents(
            onReset = {},
            onDecrement = {},
            onIncrement = {},
            onSave = {},
            onClearHistory = {}
        ),
        showClearConfirmation = false,
        onClearConfirmationChange = {}
    )
}
