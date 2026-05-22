package com.mxvier.movieflux.robot

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.mxvier.movieflux.util.waitForView
import com.mxvier.movieflux.util.withIndex
import com.mxvier.auth.R as authR
import org.hamcrest.Matchers.allOf
import org.robolectric.shadows.ShadowLooper

class LoginRobot {

    fun waitTitle() = apply {
        onView(isRoot()).perform(waitForView(allOf(withId(authR.id.auth_tv_title), isDisplayed())))
    }

    fun typeUser(user: String) = apply {
        onView(withId(authR.id.auth_et_user)).perform(typeText(user), closeSoftKeyboard())
    }

    fun typePassword(password: String) = apply {
        onView(withId(authR.id.auth_et_password)).perform(typeText(password), closeSoftKeyboard())
    }

    fun clickLogin() = apply {
        onView(withId(authR.id.auth_btn_login)).perform(click())
        // UnconfinedTestDispatcher skips delay(1500) automatically
        ShadowLooper.idleMainLooper()
    }

    fun handleBiometricIfVisible() = apply {
        ShadowLooper.idleMainLooper()
        try {
            onView(withText("Agora não")).perform(click())
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
        onView(withId(authR.id.auth_tv_title)).check(matches(isDisplayed()))
    }
}

fun loginRobot(func: LoginRobot.() -> Unit) = LoginRobot().apply { func() }
