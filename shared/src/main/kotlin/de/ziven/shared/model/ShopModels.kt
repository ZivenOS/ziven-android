package de.ziven.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShoppingList(
    val id: String,
    @SerialName("weekStart") val weekStart: String,
    val status: String,
    @SerialName("budgetCents") val budgetCents: Int?,
    @SerialName("spentCents") val spentCents: Int,
    val items: List<ShopItem>,
)

@Serializable
data class ShopItem(
    val id: String,
    @SerialName("ingredientId") val ingredientId: String,
    val code: String,
    val name: LocalizedText,
    val quantity: Double,
    val unit: String,
    val category: String,
    @SerialName("estPriceCents") val estPriceCents: Int,
    val checked: Boolean,
)

@Serializable
data class GenerateListRequest(@SerialName("weekStart") val weekStart: String)

@Serializable
data class CheckItemRequest(
    val checked: Boolean,
    @SerialName("addToPantry") val addToPantry: Boolean = false,
)
