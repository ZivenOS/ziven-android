package de.ziven.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocalizedText(val de: String, val en: String)

@Serializable
data class CookMember(val id: String, val name: String)

@Serializable
data class PlanSlot(
    val id: String,
    @SerialName("weekStart") val weekStart: String,
    val day: Int,
    val slot: String,
    val kind: String,
    @SerialName("recipeId") val recipeId: String? = null,
    @SerialName("pantryItemId") val pantryItemId: String? = null,
    val label: LocalizedText? = null,
    @SerialName("cookMember") val cookMember: CookMember? = null,
    val servings: Int,
    @SerialName("recipeTitle") val recipeTitle: LocalizedText? = null,
    val cuisine: String? = null,
)

@Serializable
data class PlanView(
    @SerialName("weekStart") val weekStart: String,
    val slots: List<PlanSlot>,
)

@Serializable
data class GeneratePlanRequest(
    @SerialName("weekStart") val weekStart: String,
    val days: Int = 7,
    val replace: Boolean = false,
)

@Serializable
data class GeneratePlanResponse(val jobId: String)

@Serializable
data class ImportedPlan(
    val ok: Boolean,
    @SerialName("weekStart") val weekStart: String,
)

@Serializable
data class StartCookRequest(
    @SerialName("planSlotId") val planSlotId: String? = null,
    @SerialName("recipeId") val recipeId: String? = null,
    val servings: Int? = null,
)

@Serializable
data class CookSession(
    val id: String,
    val status: String,
    val servings: Int,
    val recipe: CookRecipe? = null,
    val steps: List<CookStep>,
    val shortage: List<ShortageItem> = emptyList(),
    @SerialName("startedAt") val startedAt: String,
)

@Serializable
data class CookRecipe(
    val id: String,
    val title: LocalizedText,
    @SerialName("ovenSettings") val ovenSettings: OvenSettings? = null,
)

@Serializable
data class OvenSettings(
    val mode: String? = null,
    val temperature: Int? = null,
    val durationMinutes: Int? = null,
)

@Serializable
data class CookStep(
    val index: Int,
    val text: LocalizedText,
    val timer: StepTimer? = null,
)

@Serializable
data class StepTimer(val seconds: Int, @SerialName("startedAt") val startedAt: String)

@Serializable
data class ShortageItem(
    val code: String,
    val name: LocalizedText,
    @SerialName("neededGrams") val neededGrams: Double,
    @SerialName("availableGrams") val availableGrams: Double,
    @SerialName("missingGrams") val missingGrams: Double,
)

@Serializable
data class CompleteCookRequest(
    val leftovers: Leftovers? = null,
)

@Serializable
data class Leftovers(
    val servings: Int,
    @SerialName("expiresOn") val expiresOn: String? = null,
)
