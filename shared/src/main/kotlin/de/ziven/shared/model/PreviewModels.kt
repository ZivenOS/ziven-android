package de.ziven.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PreviewProfile(
    val servings: Int = 2,
    val cookDays: List<Int> = listOf(0, 1, 2, 3, 4, 5, 6),
    val slots: List<String> = listOf("dinner"),
    val dietary: List<String> = emptyList(),
    val allergens: List<String>? = null,
    @SerialName("maxMinutes") val maxMinutes: Int = 45,
    @SerialName("budgetCents") val budgetCents: Int? = null,
    @SerialName("kcalTarget") val kcalTarget: Int? = null,
    val pantry: List<PreviewPantry> = emptyList(),
)

@Serializable
data class PreviewPantry(
    @SerialName("ingredientId") val ingredientId: String,
    val grams: Double,
)

@Serializable
data class PreviewSlot(
    val day: Int,
    val slot: String,
    @SerialName("recipeId") val recipeId: String,
    val servings: Int,
)

@Serializable
data class PreviewPlanRequest(
    @SerialName("weekStart") val weekStart: String,
    val profile: PreviewProfile,
    val placements: List<PreviewSlot>? = null,
)

@Serializable
data class PreviewBundle(
    @SerialName("weekStart") val weekStart: String,
    val profile: PreviewProfile,
    val placements: List<PreviewSlot>,
    val recipes: List<PreviewRecipe>,
    val shopping: List<PreviewShoppingItem>,
    val skipped: List<PreviewSkipped>,
)

@Serializable
data class PreviewRecipe(
    val id: String,
    val title: LocalizedText,
    val steps: List<LocalizedText>,
    val servings: Int,
    @SerialName("totalMinutes") val totalMinutes: Int,
    val cuisine: String,
    val tags: List<String>,
    @SerialName("ovenSettings") val ovenSettings: OvenSettings?,
    val nutrition: Macros?,
    val confidence: PreviewConfidence,
    val allergens: List<String>,
    @SerialName("allergensUnknown") val allergensUnknown: Boolean,
    val ingredients: List<PreviewIngredient>,
)

@Serializable
data class PreviewConfidence(
    val halal: String,
    val kosher: String,
)

@Serializable
data class PreviewIngredient(
    @SerialName("ingredientId") val ingredientId: String,
    val code: String,
    val name: LocalizedText,
    val category: String,
    val grams: Double,
)

@Serializable
data class PreviewShoppingItem(
    @SerialName("ingredientId") val ingredientId: String,
    val code: String,
    val name: LocalizedText,
    val category: String,
    val grams: Int,
)

@Serializable
data class PreviewSkipped(
    val day: Int,
    val slot: String,
    val reason: String,
)

@Serializable
data class PreviewCatalog(
    val recipes: List<PreviewRecipe>,
    val ingredients: List<PreviewCatalogIngredient>,
)

@Serializable
data class PreviewCatalogIngredient(
    val id: String,
    val code: String,
    val name: LocalizedText,
)
