package de.ziven.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PantryItem(
    val id: String,
    val kind: String,
    @SerialName("ingredientId") val ingredientId: String?,
    val code: String,
    val name: LocalizedText,
    val category: String,
    val barcode: String?,
    @SerialName("productName") val productName: String?,
    val brand: String?,
    val quantity: Double,
    val unit: String,
    @SerialName("gramsEquivalent") val gramsEquivalent: Double?,
    val location: String,
    @SerialName("expiresOn") val expiresOn: String?,
    @SerialName("daysToExpiry") val daysToExpiry: Int?,
    @SerialName("discardedAt") val discardedAt: String?,
    @SerialName("discardedGrams") val discardedGrams: Double?,
)

@Serializable
sealed class PantryInput

@Serializable
@SerialName("ingredient")
data class IngredientPantryInput(
    @SerialName("ingredientCode") val ingredientCode: String,
    val quantity: Double,
    val unit: String = "g",
    val location: String = "pantry",
    @SerialName("expiresOn") val expiresOn: String? = null,
) : PantryInput()

@Serializable
@SerialName("product")
data class ProductPantryInput(
    val kind: String = "product",
    val barcode: String,
    @SerialName("productName") val productName: String,
    val brand: String? = null,
    val quantity: Double,
    val unit: String = "g",
    val location: String = "pantry",
    @SerialName("expiresOn") val expiresOn: String? = null,
) : PantryInput()

@Serializable
data class PantryPatch(
    val quantity: Double? = null,
    val unit: String? = null,
    val location: String? = null,
    @SerialName("expiresOn") val expiresOn: String? = null,
)

@Serializable
data class ProductSuggestion(
    val code: String,
    val name: String,
    val brand: String?,
    @SerialName("imageUrl") val imageUrl: String?,
    @SerialName("nutriScore") val nutriScore: String?,
    val allergens: List<String>,
)

@Serializable
data class ProductSuggestions(val suggestions: List<ProductSuggestion>)

@Serializable
data class Product(
    val code: String,
    val name: String,
    val brand: String?,
    @SerialName("imageUrl") val imageUrl: String?,
    @SerialName("nutriScore") val nutriScore: String?,
    val allergens: List<String>,
    @SerialName("ingredientsText") val ingredientsText: String?,
    @SerialName("nutritionPer100g") val nutritionPer100g: NutritionPer100g?,
)

@Serializable
data class NutritionPer100g(
    val kcal: Double?,
    val protein: Double?,
    val carbs: Double?,
    val fat: Double?,
)
