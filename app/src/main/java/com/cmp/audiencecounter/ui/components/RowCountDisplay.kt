package com.cmp.audiencecounter.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cmp.audiencecounter.R

@Composable
fun RowCountDisplay(rowCounts: SnapshotStateList<Int>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display de contagem de cada fileira individual
        if (rowCounts.isNotEmpty()) {
            Text(stringResource(R.string.counts_by_row_text))
            LazyColumn(
                modifier = Modifier.height(56.dp) // Define a altura fixa da caixa
            ) {
                itemsIndexed(rowCounts) { index, count ->
                    val reversedIndex = rowCounts.size - 1 - index
                    Text(
                        stringResource(
                            R.string.row_count_tracking_text,
                            reversedIndex + 1,
                            count
                        )
                    )
                }
            }
        }
    }
}