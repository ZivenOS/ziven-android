package de.ziven.android.data.auth

import de.ziven.shared.api.ApiClient
import de.ziven.shared.model.Credentials
import de.ziven.shared.model.MobileAuthResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val client: ApiClient,
    private val store: SecureTokenStore,
) {
    fun isLoggedIn(): Boolean = !store.accessToken.isNullOrBlank()

    suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        val response = client.mobileLogin(Credentials(email, password))
        persist(response)
    }

    suspend fun register(email: String, password: String): Result<Unit> = runCatching {
        val response = client.mobileRegister(Credentials(email, password))
        persist(response)
    }

    suspend fun refresh(): Result<Unit> = runCatching {
        val token = store.refreshToken ?: throw IllegalStateException("No refresh token")
        val response = client.mobileRefresh(token)
        persist(response)
    }

    suspend fun logout() {
        store.refreshToken?.let { client.mobileLogout(it) }
        store.clear()
    }

    private fun persist(response: MobileAuthResponse) {
        store.accessToken = response.accessToken
        store.refreshToken = response.refreshToken
        store.userId = response.user.id
    }
}
