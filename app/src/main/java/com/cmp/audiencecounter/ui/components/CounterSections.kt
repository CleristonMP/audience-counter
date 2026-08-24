package com.cmp.audiencecounter.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cmp.audiencecounter.R
import com.cmp.audiencecounter.model.AudienceRecord
import com.cmp.audiencecounter.ui.theme.CounterDecrease
import com.cmp.audiencecounter.ui.theme.CounterIncrease

@Composable
fun AudienceHistorySection(
    savedAudiences: List<AudienceRecord>,
    isSaving: Boolean,
    showClearConfirmation: Boolean,
    onClearConfirmationChange: (Boolean) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SavedAudiencesDisplay(
            savedAudiences = savedAudiences,
            displayHeight = 68.dp,
            titleFontWeight = FontWeight.Bold
        )
        ClearButton(
            isEnabled = savedAudiences.isNotEmpty() && !isSaving,
            onClick = { onClearConfirmationChange(true) },
            modifier = Modifier.align(Alignment.End)
        )
        ConfirmationDialog(
            showDialog = showClearConfirmation,
            onDismiss = { onClearConfirmationChange(false) },
            onConfirm = {
                onClearHistory()
                onClearConfirmationChange(false)
            },
            title = stringResource(R.string.confirmation_dialog_title),
            message = stringResource(R.string.confirmation_dialog_message)
        )
    }
}

@Composable
fun CounterAdjustmentControls(
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    incrementButtonSize: Dp,
    spacing: Dp,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        CounterButton(
            text = "-",
            onClick = onDecrement,
            color = CounterDecrease,
            size = 60.dp,
            fontSize = 24.sp,
            cornerRadius = 16.dp
        )
        Spacer(modifier = Modifier.width(spacing))
        CounterButton(
            text = "+",
            onClick = onIncrement,
            color = CounterIncrease,
            size = incrementButtonSize,
            fontSize = 48.sp,
            cornerRadius = 16.dp
        )
    }
}

@Composable
fun DirectCounterActions(
    count: Int,
    isSaving: Boolean,
    onReset: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        ResetButton(onClick = onReset)
        SaveButton(
            onClick = onSave,
            isSaving = isSaving,
            audience = count
        )
    }
}

@Composable
fun RowCountSetup(
    isCounting: Boolean,
    rowCount: Int,
    isSaving: Boolean,
    onStart: () -> Unit,
    onRowCountChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(enabled = !isSaving, onClick = onStart) {
            Text(stringResource(R.string.start_new_count))
        }
        if (isCounting) {
            Spacer(modifier = Modifier.height(16.dp))
            NumberInputField(
                value = rowCount.takeIf { it > 0 }?.toString().orEmpty(),
                onValueChange = { input ->
                    when {
                        input.isEmpty() -> onRowCountChange(0)
                        else -> input.toIntOrNull()?.let(onRowCountChange)
                    }
                }
            )
        }
    }
}

@Composable
fun CurrentRowHeader(
    currentRow: Int,
    rowCount: Int,
    peopleInCurrentRow: Int,
    onCompleteCurrentRow: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.row_count_text, currentRow))
        Text(peopleInCurrentRow.toString(), fontSize = 32.sp)
        Spacer(modifier = Modifier.height(8.dp))
        NextRowButton(
            currentRow = currentRow,
            rowCount = rowCount,
            onClick = onCompleteCurrentRow
        )
    }
}

@Composable
fun CurrentRowActions(
    completedRowCounts: List<Int>,
    onReset: () -> Unit,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    spacing: Dp,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RowCountDisplay(completedRowCounts)
        Spacer(modifier = Modifier.height(16.dp))
        ResetButton(onClick = onReset)
        Spacer(modifier = Modifier.height(16.dp))
        CounterAdjustmentControls(
            onDecrement = onDecrement,
            onIncrement = onIncrement,
            incrementButtonSize = 98.dp,
            spacing = spacing,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun RowTotalSummary(
    totalPeople: Long,
    canSave: Boolean,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.total_people, totalPeople),
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onSave, enabled = canSave) {
            Text(stringResource(R.string.save_total))
        }
    }
}
