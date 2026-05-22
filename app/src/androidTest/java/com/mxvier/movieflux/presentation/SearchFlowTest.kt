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
import com.mxvier.movieflux.util.waitForView
import com.mxvier.search.R as searchR
import com.mxvier.auth.R as authR
import com.mxvier.movies.R as moviesR
import org.hamcrest.Matchers.anyOf

@HiltAndroidTest
class SearchFlowTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun init() {
        hiltRule.inject()
        // Login
        onView(isRoot()).perform(waitForView(withId(authR.id.auth_et_user)))
        onView(withId(authR.id.auth_et_user)).perform(typeText("admin"), closeSoftKeyboard())
        onView(withId(authR.id.auth_et_password)).perform(typeText("123456"), closeSoftKeyboard())
        onView(withId(authR.id.auth_btn_login)).perform(click())
        
        // Wait for Home and Navigate to Search
        onView(isRoot()).perform(waitForView(withId(moviesR.id.action_search)))
        onView(withId(moviesR.id.action_search)).perform(click())
    }

    @Test
    fun search_elementsAreDisplayed() {
        onView(isRoot()).perform(waitForView(withId(searchR.id.search_view)))
        onView(withId(searchR.id.search_view)).check(matches(isDisplayed()))
    }

    @Test
    fun search_canTypeQuery() {
        onView(isRoot()).perform(waitForView(withId(androidx.appcompat.R.id.search_src_text)))
        onView(withId(androidx.appcompat.R.id.search_src_text))
            .perform(typeText("Avengers"), pressImeActionButton())
        
        onView(isRoot()).perform(waitForView(anyOf(withId(searchR.id.search_rv_movies), withId(searchR.id.search_layout_error))))
        onView(anyOf(withId(searchR.id.search_rv_movies), withId(searchR.id.search_layout_error)))
            .check(matches(isDisplayed()))
    }
}
