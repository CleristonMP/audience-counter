package com.cmp.audiencecounter.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extensão para criar um DataStore
val Context.dataStore by preferencesDataStore(name = "audience_counter")

class AudienceCounterDataStore(private val context: Context) {
    private val AUDIENCE_KEY = stringPreferencesKey("saved_audiences")

    // Função para salvar as contagens
    suspend fun saveAudiences(audiences: List<Pair<String, Int>>) {
        val savedString = audiences.joinToString(";") { "${it.first},${it.second}" }
        Log.d("AudienceCounterDataStore", "Saving: $savedString")
        context.dataStore.edit { preferences ->
            preferences[AUDIENCE_KEY] = savedString
        }
    }

    // Função para recuperar as contagens
    val audiencesFlow: Flow<List<Pair<String, Int>>> = context.dataStore.data
        .map { preferences ->
            val savedString = preferences[AUDIENCE_KEY] ?: ""
            Log.d("AudienceCounterDataStore", "Loaded: $savedString")
            if (savedString.isNotEmpty()) {
                savedString.split(";").map {
                    val (date, count) = it.split(",")
                    date to count.toInt()
                }
            } else {
                emptyList()
            }
        }
}
