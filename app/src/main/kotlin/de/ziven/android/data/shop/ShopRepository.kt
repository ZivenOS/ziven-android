package de.ziven.android.data.shop

import de.ziven.android.data.auth.SecureTokenStore
import de.ziven.shared.api.ApiClient
import de.ziven.shared.model.ShoppingList
import de.ziven.shared.model.ShopItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopRepository @Inject constructor(
    private val client: ApiClient,
    private val store: SecureTokenStore,
) {
    private suspend fun token(): String = store.accessToken ?: error("Not logged in")

    suspend fun getList(weekStart: String): ShoppingList = client.getShoppingList(token(), weekStart)

    suspend fun generate(weekStart: String): ShoppingList = client.generateShoppingList(token(), weekStart)

    suspend fun check(itemId: String, checked: Boolean) {
        client.checkShopItem(token(), itemId, checked)
    }
}
