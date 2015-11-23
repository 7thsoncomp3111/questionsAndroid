package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import android.support.test.espresso.*;

import java.util.ArrayList;

import hk.ust.cse.hunkim.questionroom.question.ButtonViewAdapter;
import hk.ust.cse.hunkim.questionroom.question.Question;


/**
 * Created by hunkim on 7/15/15.
 * based on http://evgenii.com/blog/testing-activity-in-android-studio-tutorial-part-3/
 * and
 * http://developer.android.com/training/testing.html
 */
public class JoinActivityTest extends ActivityInstrumentationTestCase2<JoinActivity> {
    JoinActivity activity;
    EditText roomNameEditText;
    ImageButton joinButton;
    ArrayList<String> rooms = null;

    private static final int TIMEOUT_IN_MS = 5000;

    public JoinActivityTest() {
        super(JoinActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        activity = getActivity();

        roomNameEditText = (EditText) activity.findViewById(R.id.room_name);

        joinButton = (ImageButton) activity.findViewById(R.id.join_button);

    }

    // Edit to add test on suggestionRoom and button title

    public void testSuggestionRoom(){


        Firebase mFirebaseRef;
        final String FIREBASE_URL = "https://resplendent-inferno-9346.firebaseio.com/";

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                roomNameEditText.requestFocus();
            }
        });
        getInstrumentation().waitForIdleSync();

        rooms = activity.getRooms();
        assertNotNull("Room detected by the class should not be null", rooms);

        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation().addMonitor(MainActivity.class.getName(), null, false);

        //Request focus on the EditText field. This must be done on the UiThread because?
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                roomNameEditText.requestFocus();
            }
        });
        //Wait until all events from the MainHandler's queue are processed
        getInstrumentation().waitForIdleSync();

        //Send the room name
        getInstrumentation().sendStringSync("");
        //roomNameEditText.setText("ANDROID Test Junit");
        getInstrumentation().waitForIdleSync();
        activity.ROOM_NAME = "";
        activity.setForTest();

        //Click on the sendToReceiverButton to send the message to ReceiverActivity
        TouchUtils.clickView(this, joinButton);
        activity.ROOM_NAME = "All";
        activity.setForTest();
        TouchUtils.clickView(this, joinButton);


    }



    // End of testing for suggestion room



    /*
    public void testIntentSetting() {

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                roomNameEditText.requestFocus();
            }
        });

        getInstrumentation().sendStringSync("all");
        getInstrumentation().waitForIdleSync();

        String actualText = roomNameEditText.getText().toString();
        assertEquals("all", actualText);

        // Tap "Join" button
        // ----------------------

        TouchUtils.clickView(this, joinButton);
        //Wait until all events from the MainHandler's queue are processed
        getInstrumentation().waitForIdleSync();

        Intent intent = activity.getIntent();
        assertNotNull("Intent should be set", intent);

        assertEquals("all", intent.getStringExtra(LoginActivity.ROOM_NAME));
    }

*/
    public void testCreatingActivity() {

        //Create and add an ActivityMonitor to monitor interaction between the system and the
        //ReceiverActivity
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation().addMonitor(MainActivity.class.getName(), null, false);

        //Request focus on the EditText field. This must be done on the UiThread because?
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                roomNameEditText.requestFocus();
            }
        });
        //Wait until all events from the MainHandler's queue are processed
        getInstrumentation().waitForIdleSync();
        //activity.setForTest();
        //Send the room name
        getInstrumentation().sendStringSync("");
        //roomNameEditText.setText("ANDROID Test Junit");
        getInstrumentation().waitForIdleSync();

        //Click on the sendToReceiverButton to send the message to ReceiverActivity
        TouchUtils.clickView(this, joinButton);

        //Send the room name
        getInstrumentation().sendStringSync("@(%*($*%#%");
        //roomNameEditText.setText("ANDROID Test Junit");
        getInstrumentation().waitForIdleSync();

        //Click on the sendToReceiverButton to send the message to ReceiverActivity
        TouchUtils.clickView(this, joinButton);

        //Send the room name
        getInstrumentation().sendStringSync("ANDROID Test Junit");


        //roomNameEditText.setText("ANDROID Test Junit");
        getInstrumentation().waitForIdleSync();

        //Click on the sendToReceiverButton to send the message to ReceiverActivity
        TouchUtils.clickView(this, joinButton);

        //Wait until all events from the MainHandler's queue are processed
        getInstrumentation().waitForIdleSync();

        //Wait until MainActivity was launched and get a reference to it.
        MainActivity mainActivity = (MainActivity) receiverActivityMonitor.waitForActivityWithTimeout(TIMEOUT_IN_MS);

        //Verify that MainActivity was started
        assertNotNull("ReceiverActivity is null", mainActivity);
        assertEquals("Monitor for MainActivity has not been called", 1,
                receiverActivityMonitor.getHits());
        assertEquals("Activity is of wrong type", MainActivity.class,
                mainActivity.getClass());

        /*
        //Read the message received by ReceiverActivity
        final TextView receivedMessage = (TextView) mainActivity
                .findViewById(R.id.received_message_text_view);
        //Verify that received message is correct
        assertNotNull(receivedMessage);
        assertEquals("Wrong received message", TEST_MESSAGE, receivedMessage.getText().toString());
        */

        Intent intent = mainActivity.getIntent();

        assertNotNull("Intent should be set", intent);

        assertEquals("ANDROID Test Junit", intent.getStringExtra(JoinActivity.ROOM_NAME));

        assertEquals("This is set correctly", "Room name: ANDROID Test Junit", mainActivity.getTitle());


        //Unregister monitor for ReceiverActivity
        getInstrumentation().removeMonitor(receiverActivityMonitor);

    }
    public void testOnCreateMainActivity(){


        //Request focus on the EditText field. This must be done on the UiThread because?
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation().addMonitor(MainActivity.class.getName(), null, false);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                roomNameEditText.requestFocus();
            }
        });
        //Wait until all events from the MainHandler's queue are processed
        getInstrumentation().waitForIdleSync();
        //activity.setForTest();
        //Send the room name
        getInstrumentation().sendStringSync("");
        //roomNameEditText.setText("ANDROID Test Junit");
        getInstrumentation().waitForIdleSync();
        MainActivity mainActivity = (MainActivity) receiverActivityMonitor.waitForActivityWithTimeout(TIMEOUT_IN_MS);
        activity.ROOM_NAME = "";
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);

    }

    public void testOnCreateMainActivity2(){


        //Request focus on the EditText field. This must be done on the UiThread because?
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation().addMonitor(MainActivity.class.getName(), null, false);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                roomNameEditText.requestFocus();
            }
        });
        //Wait until all events from the MainHandler's queue are processed
        getInstrumentation().waitForIdleSync();
        //activity.setForTest();
        //Send the room name
        getInstrumentation().sendStringSync("");
        //roomNameEditText.setText("ANDROID Test Junit");
        getInstrumentation().waitForIdleSync();
        MainActivity mainActivity = (MainActivity) receiverActivityMonitor.waitForActivityWithTimeout(TIMEOUT_IN_MS);
        activity.ROOM_NAME = null;
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);

    }
}
