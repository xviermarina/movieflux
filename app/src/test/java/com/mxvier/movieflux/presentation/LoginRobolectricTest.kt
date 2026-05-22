package com.mxvier.movieflux.presentation

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

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun loginFlow_ElementsAreDisplayed() {
        launchActivity<MainActivity>().use { scenario ->
            scenario.onActivity { activity ->
                val fragment = LoginFragment()
                activity.supportFragmentManager.beginTransaction()
                    .replace(com.mxvier.movieflux.R.id.app_nav_host_fragment, fragment, "login")
                    .commitNow()
            }

            loginRobot {
                waitTitle()
                checkTitleIsVisible()
            }
        }
    }

    @Test
    fun loginFlow_CanTypeCredentials() {
        launchActivity<MainActivity>().use { scenario ->
            scenario.onActivity { activity ->
                val fragment = LoginFragment()
                activity.supportFragmentManager.beginTransaction()
                    .replace(com.mxvier.movieflux.R.id.app_nav_host_fragment, fragment, "login")
                    .commitNow()
            }

            loginRobot {
                waitTitle()
                typeUser("admin")
                typePassword("123456")
                clickLogin()
            }
        }
    }
}
