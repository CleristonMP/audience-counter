package com.cmp.audiencecounter.repository

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
class DataStoreAudienceRepositoryTest {
    private val repository by lazy {
        DataStoreAudienceRepository(RuntimeEnvironment.getApplication())
    }

    @Before
    fun setUp() = runBlocking {
        repository.clearAudiences()
    }

    @After
    fun tearDown() = runBlocking {
        repository.clearAudiences()
    }

    @Test
    fun repositoryAddsAndClearsAudienceRecords() = runBlocking {
        repository.addAudience(42)

        assertEquals(listOf(42), repository.audiences.first().map { it.count })

        repository.clearAudiences()

        assertEquals(emptyList<Int>(), repository.audiences.first().map { it.count })
    }
}
