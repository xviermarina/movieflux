package com.mxvier.movieflux.robot

import androidx.compose.ui.test.junit4.ComposeTestRule
import org.robolectric.shadows.ShadowLooper

class SearchRobot(private val composeTestRule: ComposeTestRule) {

    fun waitSearchView() = apply {
        composeTestRule.waitForIdle()
    }

    fun typeSearchQuery(query: String) = apply {
        ShadowLooper.idleMainLooper()
    }

    fun checkSearchViewIsVisible() = apply {
        composeTestRule.waitForIdle()
    }

    fun checkResultsOrEmptyVisible() = apply {
        composeTestRule.waitForIdle()
    }
}

fun searchRobot(composeTestRule: ComposeTestRule, func: SearchRobot.() -> Unit) = SearchRobot(composeTestRule).apply { func() }
