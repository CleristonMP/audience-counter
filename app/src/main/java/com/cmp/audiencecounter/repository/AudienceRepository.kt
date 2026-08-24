package com.cmp.audiencecounter.repository

import com.cmp.audiencecounter.model.AudienceRecord
import kotlinx.coroutines.flow.Flow

interface AudienceRepository {
    val audiences: Flow<List<AudienceRecord>>

    suspend fun addAudience(count: Int)

    suspend fun clearAudiences()
}
