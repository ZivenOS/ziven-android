package de.ziven.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class DeleteAccountRequest(val password: String)

@Serializable
data class AccountExport(
    @SerialName("exportedAt") val exportedAt: String,
    val user: JsonObject,
    val sessions: List<JsonObject>,
    val memberships: List<JsonObject>,
    val households: List<JsonObject>,
    @SerialName("createdRecipes") val createdRecipes: List<JsonObject>,
)
