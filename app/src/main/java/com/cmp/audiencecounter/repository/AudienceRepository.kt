package com.cmp.audiencecounter.repository

import kotlinx.coroutines.flow.Flow

interface AudienceRepository {
    val audiences: Flow<List<Pair<String, Int>>>

    suspend fun addAudience(count: Int)

    suspend fun clearAudiences()
}
