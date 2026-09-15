package de.ziven.shared.api

import de.ziven.shared.model.AuthUser
import de.ziven.shared.model.CheckItemRequest
import de.ziven.shared.model.CompleteCookRequest
import de.ziven.shared.model.CookSession
import de.ziven.shared.model.AccountExport
import de.ziven.shared.model.Credentials
import de.ziven.shared.model.CreateHouseholdRequest
import de.ziven.shared.model.DeleteAccountRequest
import de.ziven.shared.model.GenerateListRequest
import de.ziven.shared.model.GeneratePlanRequest
import de.ziven.shared.model.GeneratePlanResponse
import de.ziven.shared.model.Household
import de.ziven.shared.model.HouseholdMember
import de.ziven.shared.model.HouseholdPatch
import de.ziven.shared.model.Invitation
import de.ziven.shared.model.InvitationRequest
import de.ziven.shared.model.JoinHouseholdRequest
import de.ziven.shared.model.MobileAuthResponse
import de.ziven.shared.model.NutritionWeek
import de.ziven.shared.model.OkResponse
import de.ziven.shared.model.PantryInput
import de.ziven.shared.model.PantryItem
import de.ziven.shared.model.PantryPatch
import de.ziven.shared.model.ImportPlanRequest
import de.ziven.shared.model.ImportedPlan
import de.ziven.shared.model.PlanView
import de.ziven.shared.model.PreviewBundle
import de.ziven.shared.model.PreviewCatalog
import de.ziven.shared.model.PreviewPlanRequest
import de.ziven.shared.model.Product
import de.ziven.shared.model.ProductSuggestions
import de.ziven.shared.model.ProfilePatch
import de.ziven.shared.model.ShoppingList
import de.ziven.shared.model.StartCookRequest
import de.ziven.shared.model.TokenBody
import de.ziven.shared.model.WeightEntry
import de.ziven.shared.model.WeightLogRequest
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

    // Nutrition
    suspend fun getNutritionWeek(token: String, week: String, memberId: String? = null): NutritionWeek =
        client.get("/v1/nutrition/week") {
            bearerAuth(token)
            url {
                parameters.append("week", week)
                memberId?.let { parameters.append("memberId", it) }
            }
        }.body()

    // Members / weight
    suspend fun logWeight(token: String, memberId: String, request: WeightLogRequest): WeightEntry =
        client.post("/v1/members/${memberId}/weight") {
            bearerAuth(token)
            setBody(request)
        }.body()

    suspend fun getWeightSeries(token: String, memberId: String, weeks: Int = 8): List<WeightEntry> =
        client.get("/v1/members/${memberId}/weight") {
            bearerAuth(token)
            url { parameters.append("weeks", weeks.toString()) }
        }.body()

    // Households
    suspend fun getCurrentHousehold(token: String): Household =
        client.get("/v1/households/current") { bearerAuth(token) }.body()

    suspend fun createHousehold(token: String, request: CreateHouseholdRequest): Household =
        client.post("/v1/households") { bearerAuth(token); setBody(request) }.body()

    suspend fun joinHousehold(token: String, request: JoinHouseholdRequest): Household =
        client.post("/v1/households/join") { bearerAuth(token); setBody(request) }.body()

    suspend fun updateHousehold(token: String, patch: HouseholdPatch): Household =
        client.patch("/v1/households/current") { bearerAuth(token); setBody(patch) }.body()

    suspend fun listInvitations(token: String): List<Invitation> =
        client.get("/v1/households/current/invitations") { bearerAuth(token) }.body()

    suspend fun createInvitation(token: String, request: InvitationRequest): Invitation =
        client.post("/v1/households/current/invitations") { bearerAuth(token); setBody(request) }.body()

    suspend fun updateMemberProfile(token: String, memberId: String, patch: ProfilePatch): HouseholdMember =
        client.patch("/v1/households/current/members/${memberId}/profile") {
            bearerAuth(token)
            setBody(patch)
        }.body()

    suspend fun removeMember(token: String, memberId: String): OkResponse =
        client.delete("/v1/households/current/members/${memberId}") { bearerAuth(token) }.body()

    suspend fun leaveHousehold(token: String): OkResponse =
        client.post("/v1/households/current/leave") { bearerAuth(token) }.body()

    // Account
    suspend fun exportAccount(token: String): AccountExport =
        client.get("/v1/me/export") { bearerAuth(token) }.body()

    suspend fun deleteAccount(token: String, password: String): OkResponse? =
        client.post("/v1/me/delete") { bearerAuth(token); setBody(DeleteAccountRequest(password)) }.body()

    // Preview (public, no auth)
    suspend fun previewPlan(request: PreviewPlanRequest): PreviewBundle =
        client.post("/v1/preview/plan") { setBody(request) }.body()

    suspend fun previewCatalog(): PreviewCatalog =
        client.get("/v1/preview/catalog").body()

    // Plan import
    suspend fun importPlan(token: String, request: ImportPlanRequest): ImportedPlan =
        client.post("/v1/plan/import") { bearerAuth(token); setBody(request) }.body()
}
