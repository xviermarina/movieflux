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
import com.mxvier.favorites.R as favoritesR
import com.mxvier.auth.R as authR
import com.mxvier.movies.R as moviesR
import org.hamcrest.Matchers.anyOf

@HiltAndroidTest
class FavoritesFlowTest {

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
        
        // Wait for Home and Navigate to Favorites
        onView(isRoot()).perform(waitForView(withId(moviesR.id.action_favorites)))
        onView(withId(moviesR.id.action_favorites)).perform(click())
    }

    @Test
    fun favorites_elementsAreDisplayed() {
        onView(isRoot()).perform(waitForView(withId(favoritesR.id.favorites_tv_toolbar_title)))
        onView(withId(favoritesR.id.favorites_tv_toolbar_title)).check(matches(isDisplayed()))
        onView(anyOf(withId(favoritesR.id.favorites_rv_movies), withId(favoritesR.id.favorites_layout_empty)))
            .check(matches(isDisplayed()))
    }
}
