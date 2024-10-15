package com.cmp.audiencecounter.ui.layouts

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.cmp.audiencecounter.ui.layouts.landscape.LandscapeRowCounterLayout
import com.cmp.audiencecounter.ui.layouts.portrait.PortraitRowCounterLayout
import com.cmp.audiencecounter.utils.getCurrentFormattedDate

@Composable
fun RowCounterLayout(
    savedAudiences: List<Pair<String, Int>>,
    onSaveTotal: (List<Pair<String, Int>>) -> Unit
) {
    var rowCount by remember { mutableIntStateOf(0) } // Quantidade de fileiras
    var currentRow by remember { mutableIntStateOf(1) } // Fileira atual
    var peopleInRow by remember { mutableIntStateOf(0) } // Pessoas na fileira atual
    val rowCounts = remember { mutableStateListOf<Int>() } // Armazena temporariamente as contagens

    var isCounting by remember { mutableStateOf(false) } // Controla se uma nova contagem está em andamento
    var showDialog by remember { mutableStateOf(false) }

    val formattedDateTime = getCurrentFormattedDate()

    BoxWithConstraints {
        if (maxWidth > maxHeight) {
            LandscapeRowCounterLayout(
                showDialog = showDialog,
                isCounting = isCounting,
                rowCount = rowCount,
                currentRow = currentRow,
                peopleInRow = peopleInRow,
                rowCounts = rowCounts,
                formattedDateTime = formattedDateTime,
                savedAudiences = savedAudiences,
                onSaveTotal = onSaveTotal,
                onChangeRowCount = { value -> rowCount = value },
                onChangeCurrentRow = { value -> currentRow = value },
                onChangePeopleInRow = { value -> peopleInRow = value },
                onShowingDialog = { value -> showDialog = value },
                onCounting = { value -> isCounting = value }
            )
        }
        else {
            PortraitRowCounterLayout(
                showDialog = showDialog,
                isCounting = isCounting,
                rowCount = rowCount,
                currentRow = currentRow,
                peopleInRow = peopleInRow,
                rowCounts = rowCounts,
                formattedDateTime = formattedDateTime,
                savedAudiences = savedAudiences,
                onSaveTotal = onSaveTotal,
                onChangeRowCount = { value -> rowCount = value },
                onChangeCurrentRow = { value -> currentRow = value },
                onChangePeopleInRow = { value -> peopleInRow = value },
                onShowingDialog = { value -> showDialog = value },
                onCounting = { value -> isCounting = value }
            )
        }
    }
}