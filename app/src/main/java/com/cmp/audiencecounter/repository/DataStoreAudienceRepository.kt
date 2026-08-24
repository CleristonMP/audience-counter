package com.cmp.audiencecounter.repository

import android.content.Context
import com.cmp.audiencecounter.datastore.AudienceCounterDataStore
import kotlinx.coroutines.flow.Flow

class DataStoreAudienceRepository(context: Context) : AudienceRepository {
    private val dataStore = AudienceCounterDataStore(context.applicationContext)

    override val audiences: Flow<List<Pair<String, Int>>> = dataStore.audiencesFlow

    override suspend fun addAudience(count: Int) {
        dataStore.addAudience(count)
    }

    override suspend fun clearAudiences() {
        dataStore.clearAudiences()
    }
}
