package com.cmp.audiencecounter.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.IOException
import java.util.concurrent.CancellationException

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class AudienceCounterDataStoreTest {

    private lateinit var dataStore: AudienceCounterDataStore
    private lateinit var context: Context

    @Before
    fun setUp() = runBlocking {
        context = RuntimeEnvironment.getApplication()
        dataStore = AudienceCounterDataStore(context)
        dataStore.saveAudiences(emptyList())
    }

    @After
    fun tearDown() = runBlocking {
        dataStore.saveAudiences(emptyList())
    }

    @Test
    fun saveAudiencesPersistsRecordsInTheSameOrder() = runBlocking {
        val audiences = listOf(
            "24/08/2026 10:15" to 25,
            "24/08/2026 09:30" to 18
        )

        dataStore.saveAudiences(audiences)

        assertEquals(audiences, dataStore.audiencesFlow.first())
    }

    @Test
    fun saveAudiencesKeepsOnlyTheFirstOneHundredRecords() = runBlocking {
        val audiences = (1..105).map { index ->
            "24/08/2026 10:${index.toString().padStart(2, '0')}" to index
        }

        dataStore.saveAudiences(audiences)

        assertEquals(audiences.take(100), dataStore.audiencesFlow.first())
    }

    @Test
    fun saveAudiencesWithEmptyListClearsExistingRecords() = runBlocking {
        dataStore.saveAudiences(listOf("24/08/2026 10:15" to 25))

        dataStore.saveAudiences(emptyList())

        assertEquals(emptyList<Pair<String, Int>>(), dataStore.audiencesFlow.first())
    }

    @Test
    fun audiencesFlowIgnoresMalformedRecordsAndPreservesValidRecords() = runBlocking {
        val audienceKey = stringPreferencesKey("saved_audiences")
        context.dataStore.edit { preferences ->
            preferences[audienceKey] = listOf(
                "24/08/2026 10:15,25",
                "missing-count",
                "24/08/2026 10:30,not-a-number",
                "24/08/2026 10:45,40"
            ).joinToString(";")
        }

        assertEquals(
            listOf("24/08/2026 10:15" to 25, "24/08/2026 10:45" to 40),
            dataStore.audiencesFlow.first()
        )
    }

    @Test
    fun decodeReturnsEmptyListForEmptyContent() {
        assertEquals(emptyList<Pair<String, Int>>(), AudienceSerialization.decode(""))
    }

    @Test
    fun decodeRejectsRecordsWithMissingOrInvalidFields() {
        val invalidRecords = listOf(
            "missing-count",
            ",10",
            "24/08/2026 10:15,",
            "24/08/2026 10:15,not-a-number",
            "24/08/2026 10:15,10,unexpected"
        )

        invalidRecords.forEach { invalidRecord ->
            assertEquals(
                "Record should be rejected: $invalidRecord",
                emptyList<Pair<String, Int>>(),
                AudienceSerialization.decode(invalidRecord)
            )
        }
    }

    @Test
    fun recoverFromReadFailureEmitsEmptyPreferencesForIOException() = runBlocking {
        val failingFlow = flow<Preferences> { throw IOException("Unable to read preferences") }

        assertEquals(emptyPreferences(), failingFlow.recoverFromReadFailure().first())
    }

    @Test
    fun recoverFromReadFailureDoesNotSwallowCancellation() {
        val failingFlow = flow<Preferences> { throw CancellationException("Cancelled") }

        assertThrows(CancellationException::class.java) {
            runBlocking { failingFlow.recoverFromReadFailure().first() }
        }
    }
}
