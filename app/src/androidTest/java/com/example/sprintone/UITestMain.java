package com.example.sprintone;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UITestMain {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void listGoesOverTheFold() {
        onView(withId(R.id.prev_btn)).perform(click());
        onView(withId(R.id.next_btn)).perform(click());
        onView(withId(R.id.camera_btn)).perform(click());
        onView(withId(R.id.filter_image_btn)).perform(click());


        onView(withId(R.id.captionText)).check(matches(isDisplayed()));
        onView(withId(R.id.dateTime)).check(matches(withText("TimeStamp")));
        onView(withId(R.id.captionText)).check(matches(withText("Caption")));
        onView(withId(R.id.coordinate)).check(matches(withText("Location")));
        onView(withId(R.id.edit_caption_btn)).perform(click());
        onView(withId(R.id.captionText)).check(matches(not(isDisplayed())));
        onView(withId(R.id.captionArea)).check(matches(isDisplayed()));
        onView(withId(R.id.editCaptionView)).perform(replaceText("NewCaption"), closeSoftKeyboard()).check(matches(withText("NewCaption")));
        onView(withId(R.id.save_btn)).perform(click());
        onView(withId(R.id.captionArea)).check(matches(not(isDisplayed())));
        onView(withId(R.id.captionText)).check(matches(isDisplayed()));
        onView(withId(R.id.captionText)).perform(replaceText("NewCaption")).check(matches(withText("NewCaption")));
    }

}
