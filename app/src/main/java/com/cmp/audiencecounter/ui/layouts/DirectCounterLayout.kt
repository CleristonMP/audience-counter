package com.cmp.audiencecounter.ui.layouts

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.cmp.audiencecounter.ui.layouts.landscape.LandscapeDirectCounterLayout
import com.cmp.audiencecounter.ui.layouts.portrait.PortraitDirectCounterLayout
import com.cmp.audiencecounter.utils.getCurrentFormattedDate

@Composable
fun DirectCounterLayout(
    savedAudiences: List<Pair<String, Int>>,
    onSaveAudiences: (List<Pair<String, Int>>) -> Unit
) {
    var audience by remember { mutableIntStateOf(0) }
    var isSaving by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    val formattedDateTime = getCurrentFormattedDate()

    BoxWithConstraints {
        if (maxWidth > maxHeight) {
            LandscapeDirectCounterLayout(
                savedAudiences,
                onSaveAudiences,
                audience,
                showDialog,
                isSaving,
                formattedDateTime,
                onResetCounting = { audience = 0 },
                onDecrement = { audience-- },
                onIncrement = { audience++ },
                onSaving = { value -> isSaving = value },
                onShowingDialog = { value -> showDialog = value }
            )
        }
        else {
            PortraitDirectCounterLayout(
                savedAudiences,
                onSaveAudiences,
                audience,
                showDialog,
                isSaving,
                formattedDateTime,
                onResetCounting = { audience = 0 },
                onDecrement = { audience-- },
                onIncrement = { audience++ },
                onSaving = { value -> isSaving = value },
                onShowingDialog = { value -> showDialog = value }
            )
        }
    }
}