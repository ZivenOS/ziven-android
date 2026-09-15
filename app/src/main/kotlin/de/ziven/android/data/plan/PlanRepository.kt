package de.ziven.android.data.plan

import de.ziven.android.data.auth.SecureTokenStore
import de.ziven.shared.api.ApiClient
import de.ziven.shared.model.CookSession
import de.ziven.shared.model.GeneratePlanRequest
import de.ziven.shared.model.PlanView
import de.ziven.shared.model.StartCookRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlanRepository @Inject constructor(
    private val client: ApiClient,
    private val store: SecureTokenStore,
) {
    private suspend fun token(): String = store.accessToken ?: error("Not logged in")

    suspend fun getPlan(weekStart: String): PlanView =
        client.getPlan(token(), weekStart)

    suspend fun generate(weekStart: String, days: Int = 7, replace: Boolean = false): String =
        client.generatePlan(token(), GeneratePlanRequest(weekStart, days, replace)).jobId

    suspend fun startCook(planSlotId: String): CookSession =
        client.startCook(token(), StartCookRequest(planSlotId = planSlotId))

    suspend fun completeCook(sessionId: String): CookSession =
        client.completeCook(token(), sessionId)
}
