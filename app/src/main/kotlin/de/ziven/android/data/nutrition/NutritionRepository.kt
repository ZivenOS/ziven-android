package de.ziven.android.data.nutrition

import de.ziven.android.data.auth.SecureTokenStore
import de.ziven.shared.api.ApiClient
import de.ziven.shared.model.NutritionWeek
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NutritionRepository @Inject constructor(
    private val client: ApiClient,
    private val store: SecureTokenStore,
) {
    private suspend fun token(): String = store.accessToken ?: error("Not logged in")

    suspend fun getWeek(weekStart: String, memberId: String? = null): NutritionWeek =
        client.getNutritionWeek(token(), weekStart, memberId)
}
