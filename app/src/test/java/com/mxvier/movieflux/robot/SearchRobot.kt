package com.mxvier.movieflux.robot

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.mxvier.movieflux.util.waitForView
import com.mxvier.movieflux.util.withIndex
import com.mxvier.search.R as searchR
import org.hamcrest.Matchers.anyOf
import org.hamcrest.Matchers.allOf

class SearchRobot {

    fun waitSearchView() = apply {
        onView(isRoot()).perform(waitForView(
            allOf(
                withId(searchR.id.search_view),
                isDisplayed()
            )
        ))
    }

    fun typeSearchQuery(query: String) = apply {
        onView(withId(androidx.appcompat.R.id.search_src_text))
            .perform(typeText(query), pressImeActionButton())
    }

    fun checkSearchViewIsVisible() = apply {
        onView(withId(searchR.id.search_view)).check(matches(isDisplayed()))
    }

    fun checkResultsOrEmptyVisible() = apply {
        onView(allOf(
            anyOf(withId(searchR.id.search_rv_movies), withId(searchR.id.search_layout_error)),
            isDisplayed()
        )).check(matches(isDisplayed()))
    }
}

fun searchRobot(func: SearchRobot.() -> Unit) = SearchRobot().apply { func() }

private fun id(resId: Int) = withId(resId)
