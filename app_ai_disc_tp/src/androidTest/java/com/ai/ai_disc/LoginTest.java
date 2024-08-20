package com.ai.ai_disc;

import android.app.Instrumentation;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

public class LoginTest {


    @Rule
    public ActivityTestRule<Login> mActivityTestRule = new ActivityTestRule<Login>(Login.class);

    public Login login = null;
    Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(Profile.class.getName(), null, false);

    @Before
    public void setUp() throws Exception {
        login = mActivityTestRule.getActivity();
    }

    /*
    @Test
    public void testLaunch() {
        View view = login.findViewById(R.id.edittext_username);

        assertNotNull(view);
        onView(withId(R.id.edittext_username)).perform(typeText("dataentry1"));
        onView(withId(R.id.edittext_username)).perform(closeSoftKeyboard());
       // Thread.sleep(1000);
        onView(withId(R.id.edittext_password)).perform(typeText("dataentry1@123"));
        onView(withId(R.id.edittext_password)).perform(closeSoftKeyboard());
        onView(withId(R.id.login)).perform(click());

        Activity Profile = getInstrumentation().waitForMonitorWithTimeout(monitor,5000);
        assertNotNull(Profile);

        Profile.finish();
    }

     */

    @After
    public void tearDown() throws Exception {

        login = null;
    }
}