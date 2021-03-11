package com.pixlee.pixleeandroidsdk

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.filters.LargeTest
import com.pixlee.pixleeandroidsdk.tool.MyViewAction
import com.pixlee.pixleeandroidsdk.tool.OkHttpIdlingResourceRule
import com.pixlee.pixleesdk.client.PXLClient
import com.pixlee.pixleesdk.data.api.AnalyticsEvents
import com.pixlee.pixleesdk.ui.viewholder.PXLPhotoViewHolder
import com.pixlee.pixleesdk.ui.viewholder.ProductViewHolder
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.core.StringContains
import org.junit.Rule
import org.junit.Test


/**
 * A test using the androidx.test unified API, which can execute on an Android device or locally using Robolectric.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@LargeTest
class ProductTagFragmentTest: BaseTest() {
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
    fun testTimeBasedProdutTag() {
        // Open widget
        PXLClient.autoAnalyticsEnabled = false

        // Get into list menu
        onView(withId(R.id.btKtxAlbumList)).perform(waitUntil(isDisplayed())).perform(ViewActions.click())

        // Changing filters
        onView(withId(R.id.fabFilter)).perform(waitUntil(isDisplayed())).perform(ViewActions.click())
        onView(withId(R.id.radioGroupContentTypeVideo)).perform(waitUntil(isDisplayed())).perform(ViewActions.click())
        PXLClient.autoAnalyticsEnabled = true
        onView(withId(R.id.btnApply)).perform(waitUntil(isDisplayed())).perform(ViewActions.click())

        onView(withId(R.id.tvDebugText)).perform(waitUntil(isDisplayed())).check(matches(withText(StringContains.containsString(AnalyticsEvents.openedWidget))))
        onView(withId(R.id.tvDebugText)).perform(waitUntil(isDisplayed())).check(matches(withText(StringContains.containsString(AnalyticsEvents.widgetVisible))))
        onView(withId(R.id.tvDebugText)).check(matches(withText(StringContains.containsString(AnalyticsEvents.widgetVisible))))

        // open lightbox by clicking an item in list
        onView(withId(R.id.pxlPhotoRecyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition<PXLPhotoViewHolder>(0, MyViewAction.clickChildViewWithId(R.id.vListRoot)))
        onView(withId(R.id.tvDebugTextViewer)).perform(waitUntil(isDisplayed())).check(matches(withText(StringContains.containsString(AnalyticsEvents.openedLightbox))))

        // look up the timer in Lightbox
        onView(withId(R.id.tvDebugTimerTextViewer)).perform(waitUntil(isDisplayed())).check(matches(withText("00:00")))
        // wait until loading the video starts
        onView(withId(R.id.tvDebugTimerTextViewer)).perform(waitUntil(withText(StringContains.containsString("00:01"))))//.check(matches(withText("00:01")))
        onView(isRoot()).perform(waitFor(4000))

        onView(withId(R.id.tvDebugTimerTextViewer)).perform(waitUntil(isDisplayed())).check(matches(withText("00:05")))

        onView(isRoot()).perform(waitFor(5000))
        onView(withId(R.id.tvDebugTimerTextViewer)).perform(waitUntil(isDisplayed())).check(matches(withText("00:10")))

//        var searchText = getText(onView(withId(R.id.tvDebugTimerTextViewer)))
//
//        Log.e("searchText", "searchText: ${getText(onView(withId(R.id.tvDebugTimerTextViewer)))}")
//
//        val matcher: Matcher<RecyclerView.ViewHolder> = withTimerView("00:10")
//        onView(withId(R.id.list)).perform(scrollToHolder(matcher), actionOnHolderItem(matcher, click()))
    }

    fun withTimerView(title: String): Matcher<RecyclerView.ViewHolder> {
        return object : BoundedMatcher<RecyclerView.ViewHolder, ProductViewHolder>(ProductViewHolder::class.java) {
            override fun matchesSafely(item: ProductViewHolder): Boolean {
                return item.getTimerView().text.toString() == title
            }

            override fun describeTo(description: Description) {
                description.appendText("view holder with title: $title")
            }
        }
    }

}
