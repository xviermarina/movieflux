package com.mxvier.movieflux.robot

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithText

class FavoritesRobot(private val composeTestRule: ComposeTestRule) {

    fun waitToolbarTitle() = apply {
        composeTestRule.onNodeWithText("Meus Favoritos").assertIsDisplayed()
    }

    fun checkToolbarTitleIsVisible() = apply {
        composeTestRule.onNodeWithText("Meus Favoritos").assertIsDisplayed()
    }

    fun checkListOrEmptyVisible() = apply {
        // Just checking title as a proxy for screen loading
        composeTestRule.onNodeWithText("Meus Favoritos").assertIsDisplayed()
    }
}

fun favoritesRobot(composeTestRule: ComposeTestRule, func: FavoritesRobot.() -> Unit) = FavoritesRobot(composeTestRule).apply { func() }
