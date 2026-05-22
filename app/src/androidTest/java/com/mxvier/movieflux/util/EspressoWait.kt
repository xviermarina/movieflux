package com.mxvier.movieflux.util

import android.view.View
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.util.HumanReadables
import androidx.test.espresso.util.TreeIterables
import org.hamcrest.CoreMatchers.any
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import java.util.concurrent.TimeoutException

fun withIndex(matcher: Matcher<View>, index: Int): Matcher<View> {
    return object : TypeSafeMatcher<View>() {
        var currentIndex = 0
        override fun describeTo(description: Description) {
            description.appendText("with index: $index ")
            matcher.describeTo(description)
        }
        override fun matchesSafely(view: View): Boolean {
            return if (matcher.matches(view)) currentIndex++ == index else false
        }
    }
}

fun waitForView(viewMatcher: Matcher<View>, timeout: Long = 10000): ViewAction {
    return object : ViewAction {
        override fun getConstraints(): Matcher<View> = any(View::class.java)

        override fun getDescription(): String = "wait for a specific view with matcher $viewMatcher during $timeout millis."

        override fun perform(uiController: UiController, view: View) {
            val startTime = System.currentTimeMillis()
            val endTime = startTime + timeout

            do {
                for (child in TreeIterables.breadthFirstViewTraversal(view)) {
                    if (viewMatcher.matches(child)) {
                        return
                    }
                }
                uiController.loopMainThreadForAtLeast(100)
            } while (System.currentTimeMillis() < endTime)

            throw PerformException.Builder()
                .withActionDescription(this.description)
                .withViewDescription(HumanReadables.describe(view))
                .withCause(TimeoutException("Timed out waiting for view: $viewMatcher"))
                .build()
        }
    }
}
