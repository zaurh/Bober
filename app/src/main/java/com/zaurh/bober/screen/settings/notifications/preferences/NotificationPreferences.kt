package com.zaurh.bober.screen.settings.notifications.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("notifications")


class NotificationPreferences(private val context: Context) {

    companion object {
        val MESSAGES = booleanPreferencesKey("messages")
        val LIKES = booleanPreferencesKey("likes")
        val MATCH = booleanPreferencesKey("match")
    }

    suspend fun controlMessages(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[MESSAGES] = enabled
        }
    }

    val getMessagesStatus: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[MESSAGES] ?: true
        }

    suspend fun controlLikes(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[LIKES] = enabled
        }
    }

    val getLikesStatus: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[LIKES] ?: true
        }


    suspend fun controlMatch(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[MATCH] = enabled
        }
    }

    val getMatchStatus: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[MATCH] ?: true
        }
}