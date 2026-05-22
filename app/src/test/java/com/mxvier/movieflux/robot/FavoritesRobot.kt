package com.mxvier.movieflux.robot

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.mxvier.movieflux.util.waitForView
import com.mxvier.movieflux.util.withIndex
import com.mxvier.favorites.R as favoritesR
import org.hamcrest.Matchers.anyOf
import org.hamcrest.Matchers.allOf

class FavoritesRobot {

    fun waitToolbarTitle() = apply {
        onView(isRoot()).perform(waitForView(
            allOf(
                withId(favoritesR.id.favorites_tv_toolbar_title),
                isDisplayed()
            )
        ))
    }

    fun checkToolbarTitleIsVisible() = apply {
        onView(withId(favoritesR.id.favorites_tv_toolbar_title)).check(matches(isDisplayed()))
    }

    fun checkListOrEmptyVisible() = apply {
        onView(allOf(
            anyOf(withId(favoritesR.id.favorites_rv_movies), withId(favoritesR.id.favorites_layout_empty)),
            isDisplayed()
        )).check(matches(isDisplayed()))
    }
}

fun favoritesRobot(func: FavoritesRobot.() -> Unit) = FavoritesRobot().apply { func() }
