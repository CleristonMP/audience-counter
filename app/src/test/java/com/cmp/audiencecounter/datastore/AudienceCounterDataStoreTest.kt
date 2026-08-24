package com.cmp.audiencecounter.datastore

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class AudienceCounterDataStoreTest {

    private lateinit var dataStore: AudienceCounterDataStore

    @Before
    fun setUp() = runBlocking {
        dataStore = AudienceCounterDataStore(RuntimeEnvironment.getApplication())
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
}
