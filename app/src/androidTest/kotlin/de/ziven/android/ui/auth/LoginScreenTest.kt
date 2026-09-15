package de.ziven.android.ui.auth

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun typeEmailAndClickLogin() {
        var submittedEmail = ""
        var submittedPassword = ""
        composeTestRule.setContent {
            LoginContent(
                email = "",
                password = "",
                uiState = AuthUiState.Idle,
                onEmailChange = {},
                onPasswordChange = {},
                onLogin = { submittedEmail = "a"; submittedPassword = "b" },
                onGoToRegister = {},
                onPreview = {},
            )
        }
        composeTestRule.onNodeWithText("E-Mail").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Passwort").performTextInput("password")
        composeTestRule.onNodeWithText("Anmelden").performClick()
        assert(submittedEmail.isNotEmpty())
    }
}
