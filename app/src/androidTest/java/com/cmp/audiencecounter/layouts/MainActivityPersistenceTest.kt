package com.cmp.audiencecounter.layouts

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.test.platform.app.InstrumentationRegistry
import com.cmp.audiencecounter.MainActivity
import com.cmp.audiencecounter.datastore.AudienceCounterDataStore
import com.cmp.audiencecounter.datastore.dataStore
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
class MainActivityPersistenceTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val context by lazy {
        InstrumentationRegistry.getInstrumentation().targetContext
    }

    private val audienceDataStore by lazy {
        AudienceCounterDataStore(context)
    }

    @Before
    fun clearBeforeTest() = runBlocking {
        audienceDataStore.clearAudiences()
        composeTestRule.waitForIdle()
    }

    @After
    fun clearAfterTest() = runBlocking {
        audienceDataStore.clearAudiences()
    }

    @Test
    fun savedDirectCountAppearsInPersistentHistory() {
        composeTestRule.onNodeWithText("+").performClick()
        composeTestRule.onNodeWithText("Salvar").performClick()

        composeTestRule.waitUntilAtLeastOneExists(
            matcher = hasText(" - 1 pessoas", substring = true),
            timeoutMillis = 5_000
        )
    }

    @Test
    fun malformedPersistentRecordDoesNotHideValidHistory() {
        runBlocking {
            val audienceKey = stringPreferencesKey("saved_audiences")
            context.dataStore.edit { preferences ->
                preferences[audienceKey] = "malformed;1777000000000,25"
            }
        }

        composeTestRule.waitUntilAtLeastOneExists(
            matcher = hasText(" - 25 pessoas", substring = true),
            timeoutMillis = 5_000
        )
        composeTestRule.onNodeWithText("Contagem Direta").assertExists()
    }
}
