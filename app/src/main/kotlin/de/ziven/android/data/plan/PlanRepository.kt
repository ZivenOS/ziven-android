package de.ziven.android.data.plan

import de.ziven.android.data.auth.SecureTokenStore
import de.ziven.android.data.cache.CacheStore
import de.ziven.shared.api.ApiClient
import de.ziven.shared.model.CookSession
import de.ziven.shared.model.GeneratePlanRequest
import de.ziven.shared.model.ImportPlanRequest
import de.ziven.shared.model.ImportedPlan
import de.ziven.shared.model.PlanView
import de.ziven.shared.model.PreviewBundle
import de.ziven.shared.model.StartCookRequest
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlanRepository @Inject constructor(
    private val client: ApiClient,
    private val store: SecureTokenStore,
    private val cache: CacheStore,
) {
    private val json = Json { ignoreUnknownKeys = true }
    private suspend fun token(): String = store.accessToken ?: error("Not logged in")

    suspend fun getPlan(weekStart: String): PlanView =
        client.getPlan(token(), weekStart)

    suspend fun cachedPlan(): PlanView? =
        cache.todayPlan()?.let { json.decodeFromString(it) }

    suspend fun savePlan(plan: PlanView) {
        cache.saveTodayPlan(json.encodeToString(PlanView.serializer(), plan))
    }

    suspend fun cachedCookSession(): CookSession? =
        cache.cookSession()?.let { json.decodeFromString(it) }

    suspend fun saveCookSession(session: CookSession) {
        cache.saveCookSession(json.encodeToString(CookSession.serializer(), session))
    }

    suspend fun generate(weekStart: String, days: Int = 7, replace: Boolean = false): String =
        client.generatePlan(token(), GeneratePlanRequest(weekStart, days, replace)).jobId

    suspend fun startCook(planSlotId: String): CookSession =
        client.startCook(token(), StartCookRequest(planSlotId = planSlotId))

    suspend fun completeCook(sessionId: String): CookSession =
        client.completeCook(token(), sessionId)

    suspend fun importPreview(bundle: PreviewBundle): ImportedPlan =
        client.importPlan(token(), ImportPlanRequest(bundle.weekStart, bundle.profile, bundle.placements))
}
