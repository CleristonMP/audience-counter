package com.cmp.audiencecounter.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore by preferencesDataStore(name = "audience_counter")

class AudienceCounterDataStore(private val context: Context) {
    private val audienceKey = stringPreferencesKey("saved_audiences")

    suspend fun saveAudiences(audiences: List<Pair<String, Int>>) {
        context.dataStore.edit { preferences ->
            preferences[audienceKey] = AudienceSerialization.encode(audiences.take(MAX_SAVED_AUDIENCES))
        }
    }

    val audiencesFlow: Flow<List<Pair<String, Int>>> = context.dataStore.data
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

    fun encode(audiences: List<Pair<String, Int>>): String =
        audiences.joinToString(RECORD_SEPARATOR) { (date, count) ->
            "$date$FIELD_SEPARATOR$count"
        }

    fun decode(serializedAudiences: String): List<Pair<String, Int>> {
        if (serializedAudiences.isBlank()) return emptyList()

        return serializedAudiences
            .split(RECORD_SEPARATOR)
            .mapNotNull(::decodeRecord)
    }

    private fun decodeRecord(record: String): Pair<String, Int>? {
        val fields = record.split(FIELD_SEPARATOR, limit = 2)
        if (fields.size != 2) return null

        val date = fields[0].trim()
        val count = fields[1].trim().toIntOrNull()

        return if (date.isNotEmpty() && count != null) date to count else null
    }
}

internal fun Flow<Preferences>.recoverFromReadFailure(): Flow<Preferences> =
    catch { cause ->
        if (cause is IOException) {
            emit(emptyPreferences())
        } else {
            throw cause
        }
    }
