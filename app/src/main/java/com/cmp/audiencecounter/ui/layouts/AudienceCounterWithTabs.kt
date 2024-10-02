package com.cmp.audiencecounter.ui.layouts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.cmp.audiencecounter.R

@Composable
fun AudienceCounterWithTabs(
    modifier: Modifier = Modifier,
    savedAudiences: List<Pair<String, Int>>,
    onSaveAudiences: (List<Pair<String, Int>>) -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        stringResource(R.string.direct_count_tab_title),
        stringResource(R.string.row_count_tab_title)
    )

    Column(
        modifier = modifier
    ) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = tab,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 18.sp
                        )
                    }
                )
            }
        }

        when (selectedTabIndex) {
            // Contagem Direta
            0 -> DirectCounterLayout(
                savedAudiences = savedAudiences,
                onSaveAudiences = onSaveAudiences
            )
            // Contagem por Fileira
            1 -> RowCounterLayout(
                savedAudiences = savedAudiences,
                onSaveTotal = onSaveAudiences
            )
        }
    }
}
