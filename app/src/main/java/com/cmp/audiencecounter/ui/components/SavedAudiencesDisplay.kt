package com.cmp.audiencecounter.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.cmp.audiencecounter.R

@Composable
fun SavedAudiencesDisplay(
    savedAudiences: List<Pair<String, Int>>,
    modifier: Modifier = Modifier,
    displayHeight: Dp = 68.dp,
    fontSize: TextUnit = 16.sp,
    titleFontWeight: FontWeight = FontWeight.Bold
) {
    Text(
        text = if (savedAudiences.isEmpty()) {
            stringResource(R.string.empty_audience_list)
        } else {
            stringResource(R.string.saved_audience_display_text)
        },
        fontSize = fontSize,
        fontWeight = titleFontWeight
    )
    LazyColumn(modifier = modifier.height(displayHeight)) {
        if (savedAudiences.isEmpty()) {
            item {
                Spacer(modifier = Modifier.height(displayHeight))
            }
        } else {
            items(savedAudiences) { (dateTime, count) ->
                Text(
                    stringResource(
                        R.string.saved_audiences_presentation_text,
                        dateTime,
                        count
                    )
                )
            }
        }
    }
}
