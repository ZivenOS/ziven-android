package de.ziven.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Credentials(val email: String, val password: String)

@Serializable
data class AuthUser(
    val id: String,
    val email: String,
    @SerialName("displayName") val displayName: String? = null,
    val locale: String,
    @SerialName("emailVerified") val emailVerified: Boolean,
    @SerialName("onboardingCompleted") val onboardingCompleted: Boolean,
)

@Serializable
data class MobileAuthResponse(
    val user: AuthUser,
    @SerialName("accessToken") val accessToken: String,
    @SerialName("refreshToken") val refreshToken: String,
    @SerialName("expiresIn") val expiresIn: Int,
)

@Serializable
data class TokenBody(val token: String)

@Serializable
data class OkResponse(val ok: Boolean = true)
