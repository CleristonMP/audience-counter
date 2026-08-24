package com.cmp.audiencecounter.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.cmp.audiencecounter.model.AudienceRecord
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.CancellationException

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class AudienceCounterDataStoreTest {

    private lateinit var dataStore: AudienceCounterDataStore
    private lateinit var context: Context

    @Before
    fun setUp() = runBlocking {
        context = RuntimeEnvironment.getApplication()
        dataStore = AudienceCounterDataStore(context) { FIXED_TIMESTAMP }
        dataStore.clearAudiences()
    }

    @After
    fun tearDown() = runBlocking {
        dataStore.clearAudiences()
    }

    @Test
    fun addAudiencePrependsNewRecords() = runBlocking {
        dataStore.addAudience(25)
        dataStore.addAudience(18)

        assertEquals(
            listOf(AudienceRecord(FIXED_TIMESTAMP, 18), AudienceRecord(FIXED_TIMESTAMP, 25)),
            dataStore.audiencesFlow.first()
        )
    }

    @Test
    fun addAudienceKeepsOnlyTheNewestOneHundredRecords() = runBlocking {
        (1..105).forEach { count ->
            dataStore.addAudience(count)
        }

        assertEquals((105 downTo 6).toList(), dataStore.audiencesFlow.first().map { it.count })
    }

    @Test
    fun clearAudiencesRemovesExistingRecords() = runBlocking {
        dataStore.addAudience(25)

        dataStore.clearAudiences()

        assertEquals(emptyList<AudienceRecord>(), dataStore.audiencesFlow.first())
    }

    @Test
    fun addAudienceGeneratesTimestampInsideTheSaveOperation() = runBlocking {
        var currentTimestamp = 100L
        val timestampedDataStore = AudienceCounterDataStore(context) { currentTimestamp }
        currentTimestamp = 200L

        timestampedDataStore.addAudience(25)

        assertEquals(listOf(AudienceRecord(200L, 25)), timestampedDataStore.audiencesFlow.first())
    }

    @Test
    fun concurrentAddsDoNotOverwriteEachOther() = runBlocking {
        coroutineScope {
            (1..50).map { count ->
                async { dataStore.addAudience(count) }
            }.awaitAll()
        }

        val storedCounts = dataStore.audiencesFlow.first().map { it.count }
        assertEquals(50, storedCounts.size)
        assertEquals((1..50).toSet(), storedCounts.toSet())
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
            listOf(
                AudienceRecord(parseLegacyTimestamp("24/08/2026 10:15"), 25),
                AudienceRecord(parseLegacyTimestamp("24/08/2026 10:45"), 40)
            ),
            dataStore.audiencesFlow.first()
        )
    }

    @Test
    fun decodeReturnsEmptyListForEmptyContent() {
        assertEquals(emptyList<AudienceRecord>(), AudienceSerialization.decode(""))
    }

    @Test
    fun decodeRejectsRecordsWithMissingOrInvalidFields() {
        val invalidRecords = listOf(
            "missing-count",
            ",10",
            "24/08/2026 10:15,",
            "24/08/2026 10:15,not-a-number",
            "24/08/2026 10:15,10,unexpected",
            "$FIXED_TIMESTAMP,0",
            "$FIXED_TIMESTAMP,-1",
            "-1,10"
        )

        invalidRecords.forEach { invalidRecord ->
            assertEquals(
                "Record should be rejected: $invalidRecord",
                emptyList<AudienceRecord>(),
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

    @Test
    fun serializationUsesStableNumericTimestamp() {
        val record = AudienceRecord(FIXED_TIMESTAMP, 25)

        assertEquals("$FIXED_TIMESTAMP,25", AudienceSerialization.encode(listOf(record)))
        assertEquals(listOf(record), AudienceSerialization.decode("$FIXED_TIMESTAMP,25"))
    }

    private fun parseLegacyTimestamp(value: String): Long =
        requireNotNull(
            SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).apply {
                isLenient = false
            }.parse(value)
        ).time

    private companion object {
        const val FIXED_TIMESTAMP = 1_777_000_000_000L
    }
}
