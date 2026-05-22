package com.mxvier.movieflux.presentation

import androidx.core.net.toUri
import androidx.navigation.fragment.NavHostFragment
import com.mxvier.movieflux.MainActivity
import com.mxvier.movieflux.robot.homeRobot
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import androidx.test.core.app.launchActivity
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

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun homeFlow_ElementsAreDisplayed() {
        launchActivity<MainActivity>().use { scenario ->
            scenario.onActivity { activity ->
                val navHostFragment = activity.supportFragmentManager.findFragmentById(com.mxvier.movieflux.R.id.app_nav_host_fragment) as NavHostFragment
                navHostFragment.navController.navigate("app://movies/home".toUri())
            }
            
            homeRobot {
                waitToolbarTitle()
                checkToolbarTitleVisible()
            }
        }
    }
}
