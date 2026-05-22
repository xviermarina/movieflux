package com.mxvier.movieflux.presentation

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.mxvier.movieflux.MainActivity
import com.mxvier.movieflux.robot.loginRobot
import com.mxvier.auth.presentation.view.LoginFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import androidx.test.core.app.launchActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = HiltTestApplication::class)
class LoginRobolectricTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun loginFlow_ElementsAreDisplayed() {
        composeTestRule.activity.let { activity ->
            val fragment = LoginFragment()
            activity.supportFragmentManager.beginTransaction()
                .replace(com.mxvier.movieflux.R.id.app_nav_host_fragment, fragment, "login")
                .commitNow()
        }

        loginRobot(composeTestRule) {
            waitTitle()
            checkTitleIsVisible()
        }
    }

    @Test
    fun loginFlow_CanTypeCredentials() {
        composeTestRule.activity.let { activity ->
            val fragment = LoginFragment()
            activity.supportFragmentManager.beginTransaction()
                .replace(com.mxvier.movieflux.R.id.app_nav_host_fragment, fragment, "login")
                .commitNow()
        }

        loginRobot(composeTestRule) {
            waitTitle()
            typeUser("admin")
            typePassword("123456")
            clickLogin()
        }
    }
}
