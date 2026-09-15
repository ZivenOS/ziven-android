package de.ziven.android.data.cache

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ziven_cache")

@Singleton
class CacheStore @Inject constructor(@ApplicationContext context: Context) {
    private val dataStore = context.dataStore

    private val todayPlan = stringPreferencesKey("today_plan")
    private val shoppingList = stringPreferencesKey("shopping_list")
    private val cookSession = stringPreferencesKey("cook_session")

    suspend fun saveTodayPlan(json: String) { dataStore.edit { it[todayPlan] = json } }
    suspend fun todayPlan(): String? = dataStore.data.map { it[todayPlan] }.firstOrNull()

    suspend fun saveShoppingList(json: String) { dataStore.edit { it[shoppingList] = json } }
    suspend fun shoppingList(): String? = dataStore.data.map { it[shoppingList] }.firstOrNull()

    suspend fun saveCookSession(json: String) { dataStore.edit { it[cookSession] = json } }
    suspend fun cookSession(): String? = dataStore.data.map { it[cookSession] }.firstOrNull()
}
