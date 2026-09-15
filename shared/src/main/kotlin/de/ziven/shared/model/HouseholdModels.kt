package de.ziven.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Household(
    val id: String,
    val name: String,
    @SerialName("preferredStore") val preferredStore: String?,
    val members: List<HouseholdMember>,
)

@Serializable
data class HouseholdMember(
    val id: String,
    @SerialName("userId") val userId: String,
    val email: String,
    val role: String,
    @SerialName("displayName") val displayName: String?,
    val dietary: List<String>,
    val dislikes: List<String>,
    @SerialName("kcalTarget") val kcalTarget: Int?,
    @SerialName("proteinTarget") val proteinTarget: Int?,
    @SerialName("carbsTarget") val carbsTarget: Int?,
    @SerialName("fatTarget") val fatTarget: Int?,
    val sex: String?,
    @SerialName("birthYear") val birthYear: Int?,
    @SerialName("heightCm") val heightCm: Int?,
    @SerialName("weightKg") val weightKg: Double?,
    @SerialName("activityLevel") val activityLevel: String?,
    val goal: String?,
    @SerialName("suggestedKcal") val suggestedKcal: Int?,
    @SerialName("joinedAt") val joinedAt: String,
)

@Serializable
data class HouseholdPatch(
    val name: String? = null,
    @SerialName("preferredStore") val preferredStore: String? = null,
)

@Serializable
data class ProfilePatch(
    @SerialName("displayName") val displayName: String? = null,
    val dietary: List<String>? = null,
    val dislikes: List<String>? = null,
    @SerialName("kcalTarget") val kcalTarget: Int? = null,
    @SerialName("proteinTarget") val proteinTarget: Int? = null,
    @SerialName("carbsTarget") val carbsTarget: Int? = null,
    @SerialName("fatTarget") val fatTarget: Int? = null,
    val sex: String? = null,
    @SerialName("birthYear") val birthYear: Int? = null,
    @SerialName("heightCm") val heightCm: Int? = null,
    @SerialName("weightKg") val weightKg: Double? = null,
    @SerialName("activityLevel") val activityLevel: String? = null,
    val goal: String? = null,
)

@Serializable
data class Invitation(
    val id: String,
    val email: String,
    val role: String,
    @SerialName("expiresAt") val expiresAt: String,
    @SerialName("createdAt") val createdAt: String,
)

@Serializable
data class InvitationRequest(val email: String, val role: String = "member")

@Serializable
data class JoinHouseholdRequest(val token: String)

@Serializable
data class CreateHouseholdRequest(val name: String)

@Serializable
data class WeightEntry(val loggedOn: String, val kg: Double)

@Serializable
data class WeightLogRequest(val kg: Double, @SerialName("loggedOn") val loggedOn: String? = null)
