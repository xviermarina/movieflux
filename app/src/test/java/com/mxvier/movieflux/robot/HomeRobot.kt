package com.mxvier.movieflux.robot

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.mxvier.movieflux.util.waitForView
import com.mxvier.movieflux.util.withIndex
import com.mxvier.movies.R as moviesR
import org.hamcrest.Matchers.allOf

class HomeRobot {

    fun waitToolbarTitle() = apply {
        onView(isRoot()).perform(waitForView(
            allOf(
                withId(moviesR.id.movies_tv_toolbar_title),
                isDisplayed()
            )
        ))
    }

    fun checkToolbarTitleVisible() = apply {
        onView(withId(moviesR.id.movies_tv_toolbar_title)).check(matches(isDisplayed()))
    }

    fun checkRecyclerViewIsVisible() = apply {
        onView(withId(moviesR.id.movies_rv_movies)).check(matches(isDisplayed()))
    }
}

fun homeRobot(func: HomeRobot.() -> Unit) = HomeRobot().apply { func() }
