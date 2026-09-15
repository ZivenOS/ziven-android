package de.ziven.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImportPlanRequest(
    @SerialName("weekStart") val weekStart: String,
    val profile: PreviewProfile,
    val placements: List<PreviewSlot>,
)
