package com.cmp.audiencecounter.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.cmp.audiencecounter.model.AudienceRecord
import com.cmp.audiencecounter.model.MAX_AUDIENCE_COUNT
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

val Context.dataStore by preferencesDataStore(name = "audience_counter")

class AudienceCounterDataStore(
    private val context: Context,
    private val currentTimeProvider: () -> Long = System::currentTimeMillis
) {
    private val audienceKey = stringPreferencesKey("saved_audiences")

    suspend fun addAudience(count: Int) {
        require(count in 1..MAX_AUDIENCE_COUNT) { "Audience count is out of range" }
        context.dataStore.edit { preferences ->
            val savedAudiences = AudienceSerialization.decode(preferences[audienceKey].orEmpty())
            val updatedAudiences = listOf(AudienceRecord(currentTimeProvider(), count)) + savedAudiences

            preferences[audienceKey] = AudienceSerialization.encode(
                updatedAudiences.take(MAX_SAVED_AUDIENCES)
            )
        }
    }

    suspend fun clearAudiences() {
        context.dataStore.edit { preferences ->
            preferences.remove(audienceKey)
        }
    }

    val audiencesFlow: Flow<List<AudienceRecord>> = context.dataStore.data
        .recoverFromReadFailure()
        .map { preferences ->
            AudienceSerialization.decode(preferences[audienceKey].orEmpty())
        }

    private companion object {
        const val MAX_SAVED_AUDIENCES = 100
    }
}

internal object AudienceSerialization {
    private const val RECORD_SEPARATOR = ";"
    private const val FIELD_SEPARATOR = ","

    fun encode(audiences: List<AudienceRecord>): String =
        audiences.joinToString(RECORD_SEPARATOR) { record ->
            "${record.timestampMillis}$FIELD_SEPARATOR${record.count}"
        }

    fun decode(serializedAudiences: String): List<AudienceRecord> {
        if (serializedAudiences.isBlank()) return emptyList()

        return serializedAudiences
            .split(RECORD_SEPARATOR)
            .mapNotNull(::decodeRecord)
    }

    private fun decodeRecord(record: String): AudienceRecord? {
        val fields = record.split(FIELD_SEPARATOR, limit = 2)
        if (fields.size != 2) return null

        val timestamp = fields[0].trim().toLongOrNull()
            ?: parseLegacyTimestamp(fields[0].trim())
        val count = fields[1].trim().toIntOrNull()

        return if (timestamp != null && timestamp >= 0 && count != null && count > 0) {
            AudienceRecord(timestamp, count)
        } else {
            null
        }
    }

    private fun parseLegacyTimestamp(value: String): Long? =
        runCatching {
            SimpleDateFormat(LEGACY_TIMESTAMP_PATTERN, Locale.getDefault()).apply {
                isLenient = false
            }.parse(value)?.time
        }.getOrNull()

    private const val LEGACY_TIMESTAMP_PATTERN = "dd/MM/yyyy HH:mm"
}

internal fun Flow<Preferences>.recoverFromReadFailure(): Flow<Preferences> =
    catch { cause ->
        if (cause is IOException) {
            emit(emptyPreferences())
        } else {
            throw cause
        }
    }
