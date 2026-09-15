package de.ziven.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Macros(
    val kcal: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
)

@Serializable
data class NutritionDay(
    val date: String,
    val actual: Macros,
)

@Serializable
data class NutritionWeek(
    @SerialName("weekStart") val weekStart: String,
    @SerialName("memberId") val memberId: String,
    val target: Macros?,
    val days: List<NutritionDay>,
    val week: NutritionWeekSummary,
    val basis: String,
)

@Serializable
data class NutritionWeekSummary(
    val actual: Macros,
    val target: Macros?,
)
