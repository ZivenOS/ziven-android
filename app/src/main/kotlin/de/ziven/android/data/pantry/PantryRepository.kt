package de.ziven.android.data.pantry

import de.ziven.android.data.auth.SecureTokenStore
import de.ziven.shared.api.ApiClient
import de.ziven.shared.model.PantryInput
import de.ziven.shared.model.PantryItem
import de.ziven.shared.model.PantryPatch
import de.ziven.shared.model.ProductPantryInput
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PantryRepository @Inject constructor(
    private val client: ApiClient,
    private val store: SecureTokenStore,
) {
    private suspend fun token(): String = store.accessToken ?: error("Not logged in")

    suspend fun list(): List<PantryItem> = client.listPantry(token())

    suspend fun addProduct(barcode: String, productName: String, brand: String?, quantity: Double, unit: String): PantryItem =
        client.addPantryItem(
            token(),
            ProductPantryInput(
                barcode = barcode,
                productName = productName,
                brand = brand,
                quantity = quantity,
                unit = unit,
            ),
        )

    suspend fun update(itemId: String, patch: PantryPatch): PantryItem =
        client.updatePantryItem(token(), itemId, patch)

    suspend fun discard(itemId: String): PantryItem =
        client.discardPantryItem(token(), itemId)

    suspend fun delete(itemId: String) {
        client.deletePantryItem(token(), itemId)
    }

    suspend fun lookupBarcode(code: String) = client.getProduct(token(), code)
}
