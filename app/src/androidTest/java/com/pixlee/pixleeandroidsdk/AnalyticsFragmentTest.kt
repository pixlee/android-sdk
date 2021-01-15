package com.pixlee.pixleeandroidsdk

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.pixlee.pixleeandroidsdk.tool.OkHttpIdlingResourceRule
import com.pixlee.pixleeandroidsdk.tool.ScrollToBottomAction
import com.pixlee.pixleesdk.data.api.AnalyticsEvents
import org.hamcrest.core.StringContains
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A test using the androidx.test unified API, which can execute on an Android device or locally using Robolectric.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class AnalyticsFragmentTest {
    /**
     * Use [ActivityScenarioRule] to create and launch the activity under test before each test,
     * and close it after each test. This is a replacement for
     * [androidx.test.rule.ActivityTestRule].
     */
    @get:Rule
    var activityScenarioRule = activityScenarioRule<MainActivity>()

    @get:Rule
    var rule = OkHttpIdlingResourceRule()


    @Test
    fun testList() {
        val buttonId = R.id.tvDebugText

        onView(withId(R.id.btKtxAlbumList)).perform(ViewActions.click())
        onView(withId(buttonId)).check(matches(withText(StringContains.containsString(AnalyticsEvents.openedWidget))))
        onView(withId(buttonId)).check(matches(withText(StringContains.containsString(AnalyticsEvents.widgetVisible))))
        onView(withId(R.id.pxlPhotoRecyclerView)).perform(ScrollToBottomAction())
        onView(withId(buttonId)).check(matches(withText(StringContains.containsString(AnalyticsEvents.loadMore))))
    }
}
