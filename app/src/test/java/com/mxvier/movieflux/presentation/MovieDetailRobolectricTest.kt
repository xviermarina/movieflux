package com.mxvier.movieflux.presentation

import android.os.Bundle
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.mxvier.movieflux.MainActivity
import com.mxvier.movieflux.robot.movieDetailRobot
import com.mxvier.movies.details.presentation.MovieDetailFragment
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = HiltTestApplication::class)
class MovieDetailRobolectricTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun movieDetailFlow_ElementsAreDisplayed() {
        composeTestRule.activity.let { activity ->
            val fragment = MovieDetailFragment().apply {
                arguments = Bundle().apply { putInt("movieId", 1) }
            }
            activity.supportFragmentManager.beginTransaction()
                .replace(com.mxvier.movieflux.R.id.app_nav_host_fragment, fragment)
                .commitNow()
        }
        ShadowLooper.idleMainLooper()
        
        movieDetailRobot(composeTestRule) {
            waitMovieTitle()
            checkMovieTitleIsVisible()
            checkOverviewLabelIsVisible()
        }
    }
}
