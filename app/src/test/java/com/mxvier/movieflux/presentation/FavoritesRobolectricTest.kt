package com.mxvier.movieflux.presentation

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.mxvier.favorites.presentation.view.FavoritesFragment
import com.mxvier.movieflux.MainActivity
import com.mxvier.movieflux.robot.favoritesRobot
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
class FavoritesRobolectricTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun favoritesFlow_ElementsAreDisplayed() {
        composeTestRule.activity.let { activity ->
            val fragment = FavoritesFragment()
            activity.supportFragmentManager.beginTransaction()
                .replace(com.mxvier.movieflux.R.id.app_nav_host_fragment, fragment)
                .commitNow()
        }
        ShadowLooper.idleMainLooper()
        
        favoritesRobot(composeTestRule) {
            waitToolbarTitle()
            checkToolbarTitleIsVisible()
            checkListOrEmptyVisible()
        }
    }
}
