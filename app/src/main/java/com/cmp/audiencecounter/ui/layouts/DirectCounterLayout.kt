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

@Composable
fun DirectCounterLayout(
    savedAudiences: List<Pair<String, Int>>,
    isPersisting: Boolean,
    onAddAudience: (Int, onSuccess: () -> Unit) -> Unit,
    onClearAudiences: () -> Unit
) {
    var audience by remember { mutableIntStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }

    BoxWithConstraints {
        if (maxWidth > maxHeight) {
            LandscapeDirectCounterLayout(
                savedAudiences = savedAudiences,
                audience = audience,
                showDialog = showDialog,
                isSaving = isPersisting,
                onResetCounting = { if (!isPersisting) audience = 0 },
                onDecrement = { if (!isPersisting) audience-- },
                onIncrement = { if (!isPersisting) audience++ },
                onSave = {
                    onAddAudience(audience) { audience = 0 }
                },
                onClearAudiences = onClearAudiences,
                onShowingDialog = { value -> showDialog = value }
            )
        } else {
            PortraitDirectCounterLayout(
                savedAudiences = savedAudiences,
                audience = audience,
                showDialog = showDialog,
                isSaving = isPersisting,
                onResetCounting = { if (!isPersisting) audience = 0 },
                onDecrement = { if (!isPersisting) audience-- },
                onIncrement = { if (!isPersisting) audience++ },
                onSave = {
                    onAddAudience(audience) { audience = 0 }
                },
                onClearAudiences = onClearAudiences,
                onShowingDialog = { value -> showDialog = value }
            )
        }
    }
}
