package com.mxvier.movieflux.robot

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.mxvier.movieflux.util.waitForView
import com.mxvier.movieflux.util.withIndex
import com.mxvier.movies.R as moviesR
import org.hamcrest.Matchers.allOf

class MovieDetailRobot {

    fun waitMovieTitle() = apply {
        onView(isRoot()).perform(waitForView(allOf(withId(moviesR.id.movies_tv_movie_title), isDisplayed())))
    }

    fun checkMovieTitleIsVisible() = apply {
        onView(withId(moviesR.id.movies_tv_movie_title)).check(matches(isDisplayed()))
    }

    fun checkOverviewLabelIsVisible() = apply {
        onView(withId(moviesR.id.movies_tv_overview_label)).check(matches(isDisplayed()))
    }

    fun checkFavoriteButtonIsVisible() = apply {
        onView(withId(moviesR.id.movies_btn_favorite_content)).check(matches(isDisplayed()))
    }
}

fun movieDetailRobot(func: MovieDetailRobot.() -> Unit) = MovieDetailRobot().apply { func() }
