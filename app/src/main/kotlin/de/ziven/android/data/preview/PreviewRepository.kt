package de.ziven.android.data.preview

import de.ziven.shared.api.ApiClient
import de.ziven.shared.model.PreviewBundle
import de.ziven.shared.model.PreviewCatalog
import de.ziven.shared.model.PreviewPlanRequest
import de.ziven.shared.model.PreviewProfile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreviewRepository @Inject constructor(
    private val client: ApiClient,
) {
    suspend fun previewPlan(weekStart: String, profile: PreviewProfile): PreviewBundle =
        client.previewPlan(PreviewPlanRequest(weekStart, profile))

    suspend fun catalog(): PreviewCatalog = client.previewCatalog()
}
