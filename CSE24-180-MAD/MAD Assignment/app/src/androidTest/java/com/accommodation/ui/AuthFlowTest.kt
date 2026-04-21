package com.accommodation.ui

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.accommodation.data.database.AppDatabase
import com.accommodation.data.repository.UserRepository
import com.accommodation.ui.auth.AuthViewModel
import com.accommodation.ui.auth.LoginScreen
import com.accommodation.ui.auth.RegisterScreen
import com.accommodation.ui.theme.AccommodationTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthFlowTest {

    @get:Rule val composeRule = createComposeRule()

    private lateinit var viewModel: AuthViewModel

    @Before fun setUp() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(ctx, AppDatabase::class.java).build()
        viewModel = AuthViewModel(UserRepository(db.userDao()))
    }

    @Test fun loginScreen_showsEmailAndPasswordFields() {
        composeRule.setContent {
            AccommodationTheme { LoginScreen(viewModel = viewModel, onSuccess = { _, _ -> }, onNavigateToRegister = {}) }
        }
        composeRule.onNodeWithText("Email").assertIsDisplayed()
        composeRule.onNodeWithText("Password").assertIsDisplayed()
    }

    @Test fun loginScreen_showsErrorOnInvalidCredentials() {
        composeRule.setContent {
            AccommodationTheme { LoginScreen(viewModel = viewModel, onSuccess = { _, _ -> }, onNavigateToRegister = {}) }
        }
        composeRule.onNodeWithText("Email").performTextInput("nobody@ub.bw")
        composeRule.onNodeWithText("Password").performTextInput("Pass@123")
        composeRule.onNodeWithText("Login").performClick()
        composeRule.waitUntil(3000) {
            composeRule.onAllNodesWithText("Invalid email or password").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Invalid email or password").assertIsDisplayed()
    }

    @Test fun registerScreen_showsValidationErrorForShortPassword() {
        composeRule.setContent {
            AccommodationTheme { RegisterScreen(viewModel = viewModel, onSuccess = { _, _ -> }, onNavigateToLogin = {}) }
        }
        composeRule.onNodeWithText("Email").performTextInput("new@ub.bw")
        composeRule.onNodeWithText("Password").performTextInput("short")
        composeRule.onNodeWithText("Register").performClick()
        composeRule.waitUntil(3000) {
            composeRule.onAllNodesWithText("Password must be 8+ chars with letter, digit, and special character").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Password must be 8+ chars with letter, digit, and special character").assertIsDisplayed()
    }
}
