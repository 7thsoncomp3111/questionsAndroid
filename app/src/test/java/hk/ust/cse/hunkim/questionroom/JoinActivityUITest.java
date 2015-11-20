package hk.ust.cse.hunkim.questionroom;
/*
 * Copyright 2015, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/**
 * Basic tests showcasing simple view matchers and actions like {@link ViewMatchers#withId},
 * {@link ViewActions#click} and {@link ViewActions#typeText}.
 * <p/>
 * Note that there is no need to tell Espresso that a view is in a different {@link Activity}.
 */

/*
@RunWith(AndroidJUnit4.class)
@LargeTest
public class JoinActivityUITest extends ActivityInstrumentationTestCase2 <JoinActivity>{

    public JoinActivityUITest () {
        super(JoinActivity.class);
    }

    public static final String STRING_TO_BE_TYPED = "all";


    @Rule
    public ActivityTestRule<JoinActivity> mActivityRule = new ActivityTestRule<>(
            JoinActivity.class);

    public JoinActivityUITest(Class<JoinActivity> activityClass) {
        super(activityClass);
    }

    @Test
    public void testchangeText_sameActivity() {
        // Type text and then press the button.
        onView(withId(R.id.room_name))
                .perform(typeText(STRING_TO_BE_TYPED), closeSoftKeyboard());
        onView(withId(R.id.join_button)).perform(click());

        // Check that the text was changed.
        onView(withId(R.id.room_name)).check(matches(withText(STRING_TO_BE_TYPED)));
    }

    @Test
    public void changeText_newActivity() {
        // Type text and then press the button.
        onView(withId(R.id.room_name)).perform(typeText(STRING_TO_BE_TYPED),
                closeSoftKeyboard());
        onView(withId(R.id.join_button)).perform(click());

        // This view is in a different Activity, no need to tell Espresso.
        onView(withId(R.id.messageInput)).check(matches(withText(STRING_TO_BE_TYPED)));
    }
}
*/