package com.mxvier.movieflux.presentation

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.mxvier.movieflux.MainActivity
import com.mxvier.movieflux.robot.homeRobot
import com.mxvier.movies.home.presentation.view.HomeFragment
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
class HomeRobolectricTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun homeFlow_ElementsAreDisplayed() {
        composeTestRule.activity.let { activity ->
            val fragment = HomeFragment()
            activity.supportFragmentManager.beginTransaction()
                .replace(com.mxvier.movieflux.R.id.app_nav_host_fragment, fragment)
                .commitNow()
        }
        ShadowLooper.idleMainLooper()
        
        homeRobot(composeTestRule) {
            waitToolbarTitle()
            checkToolbarTitleVisible()
        }
    }
}
