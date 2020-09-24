package com.example.sprintone;


import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UITestFilter {
    @Rule
    public ActivityTestRule<FilterGalleryActivity> activityRule = new ActivityTestRule<>(FilterGalleryActivity.class, true, false);

    @Test
    public void listGoesOverTheFold() {
        Intent intent = new Intent();
        intent.putExtra("EXTRA", "Test");
        activityRule.launchActivity(intent);

        onView(withId(R.id.start_date_text)).check(matches(withText("START_DATE")));
        onView(withId(R.id.end_date_text)).check(matches(withText("END_DATE")));
        onView(withId(R.id.Coordinate)).check(matches(withText("Coordinates")));
        onView(withId(R.id.Latitude)).check(matches(withText("Lat")));
        onView(withId(R.id.Longitude)).check(matches(withText("Lon")));
        onView(withId(R.id.top_left)).check(matches(withText("Top Left")));
        onView(withId(R.id.bottom_right)).check(matches(withText("Bottom Right")));
        onView(withId(R.id.keyword_text)).check(matches(withText("KEYWORD")));

        onView(withId(R.id.filter_start_date)).perform(replaceText("2020-09-21 00:00:00"), closeSoftKeyboard()).check(matches(withText("2020-09-21 00:00:00")));
        onView(withId(R.id.filter_end_date)).perform(replaceText("2020-09-22 00:00:00"), closeSoftKeyboard()).check(matches(withText("2020-09-22 00:00:00")));
        onView(withId(R.id.filter_keyword)).perform(typeText("caption"), closeSoftKeyboard()).check(matches(withText("caption")));

        onView(withId(R.id.search_filter_btn)).perform(click());
        onView(withId(R.id.cancel_filter_btn)).perform(click());
    }
}
