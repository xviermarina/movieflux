package com.mxvier.movieflux.presentation

import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.mxvier.movieflux.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.mxvier.auth.R as authR

@HiltAndroidTest
class LoginFlowTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun login_elementsAreDisplayed() {
        onView(withId(authR.id.auth_tv_title)).check(matches(isDisplayed()))
        onView(withId(authR.id.auth_et_user)).check(matches(isDisplayed()))
        onView(withId(authR.id.auth_et_password)).check(matches(isDisplayed()))
    }
}
