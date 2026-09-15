package de.ziven.android.data.account

import de.ziven.android.data.auth.SecureTokenStore
import de.ziven.shared.api.ApiClient
import de.ziven.shared.model.AccountExport
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository @Inject constructor(
    private val client: ApiClient,
    private val store: SecureTokenStore,
) {
    private suspend fun token(): String = store.accessToken ?: error("Not logged in")

    suspend fun export(): AccountExport = client.exportAccount(token())

    suspend fun delete(password: String): Boolean {
        client.deleteAccount(token(), password)
        store.clear()
        return true
    }
}
