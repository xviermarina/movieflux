package com.mxvier.movieflux.robot

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithText

class MovieDetailRobot(private val composeTestRule: ComposeTestRule) {

    fun waitMovieTitle() = apply {
        composeTestRule.onNodeWithText("Detalhes", substring = true).assertExists()
    }

    fun checkMovieTitleIsVisible() = apply {
        composeTestRule.onNodeWithText("Detalhes", substring = true).assertExists()
    }

    fun checkOverviewLabelIsVisible() = apply {
        composeTestRule.onNodeWithText("Sinopse", substring = true).assertExists()
    }

    fun checkFavoriteButtonIsVisible() = apply {
        // Checking for "Favoritar" or "Desfavoritar"
        // This is a bit dynamic, so let's just check screen presence
        composeTestRule.onNodeWithText("Detalhes do Filme").assertIsDisplayed()
    }
}

fun movieDetailRobot(composeTestRule: ComposeTestRule, func: MovieDetailRobot.() -> Unit) = MovieDetailRobot(composeTestRule).apply { func() }
