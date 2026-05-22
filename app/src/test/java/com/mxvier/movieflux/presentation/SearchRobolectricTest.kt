package com.mxvier.movieflux.presentation

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.mxvier.movieflux.MainActivity
import com.mxvier.movieflux.robot.searchRobot
import com.mxvier.search.presentation.SearchFragment
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
class SearchRobolectricTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun searchFlow_ElementsAreDisplayed() {
        composeTestRule.activity.let { activity ->
            val fragment = SearchFragment()
            activity.supportFragmentManager.beginTransaction()
                .replace(com.mxvier.movieflux.R.id.app_nav_host_fragment, fragment)
                .commitNow()
        }
        ShadowLooper.idleMainLooper()
        
        searchRobot(composeTestRule) {
            waitSearchView()
            checkSearchViewIsVisible()
        }
    }

    @Test
    fun searchFlow_CanTypeQuery() {
        composeTestRule.activity.let { activity ->
            val fragment = SearchFragment()
            activity.supportFragmentManager.beginTransaction()
                .replace(com.mxvier.movieflux.R.id.app_nav_host_fragment, fragment)
                .commitNow()
        }
        ShadowLooper.idleMainLooper()
        
        searchRobot(composeTestRule) {
            waitSearchView()
            typeSearchQuery("Avengers")
            ShadowLooper.idleMainLooper()
            checkResultsOrEmptyVisible()
        }
    }
}
