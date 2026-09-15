package de.ziven.shared.api

import de.ziven.shared.model.AuthUser
import de.ziven.shared.model.Credentials
import de.ziven.shared.model.MobileAuthResponse
import de.ziven.shared.model.OkResponse
import de.ziven.shared.model.TokenBody
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
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
}
