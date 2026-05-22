package com.mxvier.movieflux.robot

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.mxvier.movieflux.util.waitForView
import com.mxvier.movieflux.util.withIndex
import com.mxvier.auth.R as authR
import org.hamcrest.Matchers.allOf
import org.robolectric.shadows.ShadowLooper

class LoginRobot(private val composeTestRule: ComposeTestRule) {

    fun waitTitle() = apply {
        composeTestRule.onNodeWithText("MovieFlux").assertExists()
    }

    fun typeUser(user: String) = apply {
        composeTestRule.onNodeWithText("Usuário").performTextInput(user)
    }

    fun typePassword(password: String) = apply {
        composeTestRule.onNodeWithText("Senha").performTextInput(password)
    }

    fun clickLogin() = apply {
        composeTestRule.onNodeWithText("Entrar").performClick()
        ShadowLooper.idleMainLooper()
    }

    fun handleBiometricIfVisible() = apply {
        ShadowLooper.idleMainLooper()
        try {
            composeTestRule.onNodeWithText("Agora não").performClick()
            ShadowLooper.idleMainLooper()
        } catch (e: Exception) {
            try {
                onView(withId(android.R.id.button2)).perform(click())
                ShadowLooper.idleMainLooper()
            } catch (e2: Exception) {}
        }
        ShadowLooper.idleMainLooper()
    }

    fun checkTitleIsVisible() = apply {
        composeTestRule.onNodeWithText("MovieFlux").assertExists()
    }
}

fun loginRobot(composeTestRule: ComposeTestRule, func: LoginRobot.() -> Unit) = LoginRobot(composeTestRule).apply { func() }
