package de.ziven.shared.api

import de.ziven.shared.model.AuthUser
import de.ziven.shared.model.CheckItemRequest
import de.ziven.shared.model.CompleteCookRequest
import de.ziven.shared.model.CookSession
import de.ziven.shared.model.Credentials
import de.ziven.shared.model.GenerateListRequest
import de.ziven.shared.model.GeneratePlanRequest
import de.ziven.shared.model.GeneratePlanResponse
import de.ziven.shared.model.MobileAuthResponse
import de.ziven.shared.model.OkResponse
import de.ziven.shared.model.PantryInput
import de.ziven.shared.model.PantryItem
import de.ziven.shared.model.PantryPatch
import de.ziven.shared.model.PlanView
import de.ziven.shared.model.Product
import de.ziven.shared.model.ProductSuggestions
import de.ziven.shared.model.ShoppingList
import de.ziven.shared.model.StartCookRequest
import de.ziven.shared.model.TokenBody
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Thin Ktor client wrapper. In a production build this class is replaced by
 * the OpenAPI-generated client under `de.ziven.shared.api.generated`; the
 * methods here scaffold the auth flow until the generator is wired.
 */
class ApiClient(baseUrl: String) {
    val json = Json { ignoreUnknownKeys = true; isLenient = true }

    val client = HttpClient(OkHttp) {
        install(ContentNegotiation) { json(json) }
        install(Logging) {
            level = LogLevel.INFO
        }
        defaultRequest {
            url(baseUrl)
            header("User-Agent", "ZivenAndroid/0.1.0")
            contentType(ContentType.Application.Json)
        }
    }

    suspend fun mobileRegister(credentials: Credentials): MobileAuthResponse =
        client.post("/v1/auth/mobile/register") { setBody(credentials) }.body()

    suspend fun mobileLogin(credentials: Credentials): MobileAuthResponse =
        client.post("/v1/auth/mobile/login") { setBody(credentials) }.body()

    suspend fun mobileRefresh(refreshToken: String): MobileAuthResponse =
        client.post("/v1/auth/mobile/refresh") { setBody(TokenBody(refreshToken)) }.body()

    suspend fun mobileLogout(refreshToken: String): OkResponse? =
        client.post("/v1/auth/mobile/logout") { setBody(TokenBody(refreshToken)) }.body()

    suspend fun me(token: String): AuthUser =
        client.get("/v1/auth/me") { bearerAuth(token) }.body()

    // Plan
    suspend fun getPlan(token: String, week: String): PlanView =
        client.get("/v1/plan") {
            bearerAuth(token)
            url { parameters.append("week", week) }
        }.body()

    suspend fun generatePlan(token: String, request: GeneratePlanRequest): GeneratePlanResponse =
        client.post("/v1/plan/generate") {
            bearerAuth(token)
            setBody(request)
        }.body()

    // Cook
    suspend fun startCook(token: String, request: StartCookRequest): CookSession =
        client.post("/v1/cook/sessions") {
            bearerAuth(token)
            setBody(request)
        }.body()

    suspend fun getCookSession(token: String, id: String): CookSession =
        client.get("/v1/cook/sessions/${id}") { bearerAuth(token) }.body()

    suspend fun completeCook(token: String, id: String, request: CompleteCookRequest = CompleteCookRequest()): CookSession =
        client.post("/v1/cook/sessions/${id}/complete") {
            bearerAuth(token)
            setBody(request)
        }.body()

    // Shop
    suspend fun getShoppingList(token: String, week: String): ShoppingList =
        client.get("/v1/shop") {
            bearerAuth(token)
            url { parameters.append("week", week) }
        }.body()

    suspend fun generateShoppingList(token: String, week: String): ShoppingList =
        client.post("/v1/shop/generate") {
            bearerAuth(token)
            setBody(GenerateListRequest(week))
        }.body()

    suspend fun checkShopItem(token: String, itemId: String, checked: Boolean): OkResponse =
        client.post("/v1/shop/items/${itemId}/check") {
            bearerAuth(token)
            setBody(CheckItemRequest(checked))
        }.body()

    // Pantry
    suspend fun listPantry(token: String): List<PantryItem> =
        client.get("/v1/pantry") { bearerAuth(token) }.body()

    suspend fun addPantryItem(token: String, input: PantryInput): PantryItem =
        client.post("/v1/pantry") {
            bearerAuth(token)
            setBody(input)
        }.body()

    suspend fun updatePantryItem(token: String, itemId: String, patch: PantryPatch): PantryItem =
        client.patch("/v1/pantry/${itemId}") {
            bearerAuth(token)
            setBody(patch)
        }.body()

    suspend fun discardPantryItem(token: String, itemId: String): PantryItem =
        client.post("/v1/pantry/${itemId}/discard") { bearerAuth(token) }.body()

    suspend fun deletePantryItem(token: String, itemId: String): OkResponse =
        client.delete("/v1/pantry/${itemId}") { bearerAuth(token) }.body()

    // Products
    suspend fun getProduct(token: String, code: String): Product =
        client.get("/v1/products/${code}") { bearerAuth(token) }.body()

    suspend fun suggestProducts(token: String, query: String): ProductSuggestions =
        client.get("/v1/products/suggest") {
            bearerAuth(token)
            url { parameters.append("q", query) }
        }.body()
}
