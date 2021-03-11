package com.pixlee.pixleeandroidsdk

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.actionWithAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.pixlee.pixleeandroidsdk.tool.MyViewAction
import com.pixlee.pixleeandroidsdk.tool.OkHttpIdlingResourceRule
import com.pixlee.pixleeandroidsdk.tool.ScrollToBottomAction
import com.pixlee.pixleesdk.client.PXLClient
import com.pixlee.pixleesdk.data.api.AnalyticsEvents
import com.pixlee.pixleesdk.network.observer.AnalyticsObserver
import com.pixlee.pixleesdk.ui.viewholder.PXLPhotoViewHolder
import org.hamcrest.Matcher
import org.hamcrest.StringDescription
import org.hamcrest.core.StringContains
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * A test using the androidx.test unified API, which can execute on an Android device or locally using Robolectric.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@LargeTest
class AnalyticsFragmentTest: BaseTest() {
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
    fun testListHavingAnalyticsOff() {
        // open widget
        PXLClient.autoAnalyticsEnabled = false
        onView(withId(R.id.btKtxAlbumList)).perform(waitUntil(isDisplayed())).perform(ViewActions.click())
        onView(withId(R.id.tvDebugText)).perform(waitUntil(isDisplayed())).check(matches(withText(StringContains.containsString(AnalyticsObserver.noEventsMessage))))
        onView(withId(R.id.tvDebugText)).perform(waitUntil(isDisplayed())).check(matches(withText(StringContains.containsString(AnalyticsObserver.noEventsMessage))))
        onView(withId(R.id.tvDebugText)).check(matches(withText(StringContains.containsString(AnalyticsObserver.noEventsMessage))))

        // open lightbox by clicking an item in list
        onView(withId(R.id.pxlPhotoRecyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition<PXLPhotoViewHolder>(0, MyViewAction.clickChildViewWithId(R.id.vListRoot)))
        onView(withId(R.id.tvDebugTextViewer)).perform(waitUntil(isDisplayed())).check(matches(withText(StringContains.containsString(AnalyticsObserver.noEventsMessage))))
    }

    @Test
    fun testList() {
        // open widget
        PXLClient.autoAnalyticsEnabled = true
        onView(withId(R.id.btKtxAlbumList)).perform(waitUntil(isDisplayed())).perform(ViewActions.click())
        onView(withId(R.id.tvDebugText)).perform(waitUntil(isDisplayed())).check(matches(withText(StringContains.containsString(AnalyticsEvents.openedWidget))))
        onView(withId(R.id.tvDebugText)).perform(waitUntil(isDisplayed())).check(matches(withText(StringContains.containsString(AnalyticsEvents.widgetVisible))))
        onView(withId(R.id.tvDebugText)).check(matches(withText(StringContains.containsString(AnalyticsEvents.widgetVisible))))

        // scroll down to load more photos
        onView(withId(R.id.pxlPhotoRecyclerView)).perform(ScrollToBottomAction())
        onView(withId(R.id.tvDebugText)).perform(waitUntil(isDisplayed())).check(matches(withText(StringContains.containsString(AnalyticsEvents.loadMore))))
    }

    @Test
    fun testGrid() {
        // open grid widget
        PXLClient.autoAnalyticsEnabled = true
        onView(withId(R.id.btKtxAlbumGrid)).perform(waitUntil(isDisplayed())).perform(ViewActions.click())
        onView(withId(R.id.tvDebugText)).perform(waitUntil(isDisplayed())).check(matches(withText(StringContains.containsString(AnalyticsEvents.openedWidget))))
        onView(withId(R.id.tvDebugText)).perform(waitUntil(isDisplayed())).check(matches(withText(StringContains.containsString(AnalyticsEvents.widgetVisible))))
    }

    @Test
    fun testProductDetail() {
        // open widget
        PXLClient.autoAnalyticsEnabled = true
        onView(withId(R.id.btKtxAlbumList)).perform(waitUntil(isDisplayed())).perform(ViewActions.click())
        onView(withId(R.id.tvDebugText)).perform(waitUntil(isDisplayed())).check(matches(withText(StringContains.containsString(AnalyticsEvents.openedWidget))))
        onView(withId(R.id.tvDebugText)).perform(waitUntil(isDisplayed())).check(matches(withText(StringContains.containsString(AnalyticsEvents.widgetVisible))))

        // open lightbox by clicking an item in list
        onView(withId(R.id.pxlPhotoRecyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition<PXLPhotoViewHolder>(0, MyViewAction.clickChildViewWithId(R.id.vListRoot)))
        onView(withId(R.id.tvDebugTextViewer)).perform(waitUntil(isDisplayed())).check(matches(withText(StringContains.containsString(AnalyticsEvents.openedLightbox))))
    }
}
