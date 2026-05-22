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
import androidx.test.espresso.contrib.RecyclerViewActions
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mxvier.auth.R as authR
import com.mxvier.movies.R as moviesR
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.Description

@HiltAndroidTest
class MovieDetailFlowTest {

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
    }

    @Test
    fun movieDetail_elementsAreDisplayedAfterClick() {
        // Wait for list and Click on first item of popular movies
        onView(isRoot()).perform(waitForView(withId(moviesR.id.movies_rv_movies)))
        onView(withId(moviesR.id.movies_rv_movies))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        
        onView(isRoot()).perform(waitForView(withId(moviesR.id.movies_tv_movie_title)))
        onView(withId(moviesR.id.movies_tv_movie_title)).check(matches(isDisplayed()))
        onView(withId(moviesR.id.movies_tv_overview_label)).check(matches(isDisplayed()))
    }
}
