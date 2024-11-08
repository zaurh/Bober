package com.zaurh.bober.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("bober")


class BoberPreferences(private val context: Context) {

    companion object {
        val JWT_KEY = stringPreferencesKey("jwt_key")
        val SIGNED_IN = booleanPreferencesKey("signed_in")
        val CURRENT_USER_ID = stringPreferencesKey("current_user_id")
    }

    suspend fun saveJwtKey(jwtKey: String) {
        context.dataStore.edit { preferences ->
            preferences[JWT_KEY] = jwtKey
        }
    }

    val getJwtKey: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[JWT_KEY] ?: ""
        }

    suspend fun saveSignedIn(signedIn: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SIGNED_IN] = signedIn
        }
    }

    val getSignedIn: Flow<Boolean?> = context.dataStore.data
        .map { preferences ->
            preferences[SIGNED_IN] ?: false
        }

    suspend fun saveCurrentUserId(currentUserId: String) {
        context.dataStore.edit { preferences ->
            preferences[CURRENT_USER_ID] = currentUserId
        }
    }

    val getCurrentUserId: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[CURRENT_USER_ID] ?: ""
        }

}