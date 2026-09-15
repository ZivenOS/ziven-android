package de.ziven.android.data.shop

import de.ziven.android.data.auth.SecureTokenStore
import de.ziven.android.data.cache.CacheStore
import de.ziven.shared.api.ApiClient
import de.ziven.shared.model.ShoppingList
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopRepository @Inject constructor(
    private val client: ApiClient,
    private val store: SecureTokenStore,
    private val cache: CacheStore,
) {
    private val json = Json { ignoreUnknownKeys = true }
    private suspend fun token(): String = store.accessToken ?: error("Not logged in")

    suspend fun getList(weekStart: String): ShoppingList = client.getShoppingList(token(), weekStart)

    suspend fun cachedList(): ShoppingList? =
        cache.shoppingList()?.let { json.decodeFromString(it) }

    suspend fun saveList(list: ShoppingList) {
        cache.saveShoppingList(json.encodeToString(ShoppingList.serializer(), list))
    }

    suspend fun generate(weekStart: String): ShoppingList = client.generateShoppingList(token(), weekStart)

    suspend fun check(itemId: String, checked: Boolean) {
        client.checkShopItem(token(), itemId, checked)
    }
}
