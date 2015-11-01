package hk.ust.cse.hunkim.questionroom;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

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
    GridView gridSuggestion;

    private static final int TIMEOUT_IN_MS = 5000;
    private TextView roomNameView;
    public JoinActivityTest() {
        super(JoinActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        activity = getActivity();

        roomNameEditText =
                (EditText) activity.findViewById(R.id.room_name);

        joinButton =
                (ImageButton) activity.findViewById(R.id.join_button);

        // Edit testing for suggestionRoom getView

        gridSuggestion = (GridView) activity.findViewById(R.id.List2View);
        testSuggestionRoom();
        testCreateButtonforGridview();

    }

    // Edit to add test on suggestionRoom and button title

    public void testSuggestionRoom(){


        Firebase mFirebaseRef;
        final String FIREBASE_URL = "https://resplendent-inferno-9346.firebaseio.com/";

        JoinActivity m = new JoinActivity();
        final ArrayList<String> rooms = null;
        ArrayList<String> oldRoom = m.getRooms();


        mFirebaseRef = new Firebase(FIREBASE_URL).child("room");



        mFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    rooms.add(postSnapshot.getKey());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        assertEquals("Room detected by the class and room from firebase",rooms,oldRoom);

    }

    public void testCreateButtonforGridview(){

        roomNameView = roomNameEditText;
        ArrayList<String> rooms = new ArrayList<String>();
        String testRoomName = "Test Room Name";
        rooms.add(testRoomName);
        JoinActivity m = new JoinActivity();
        Button btn = (Button) activity.findViewById(R.id.suggestionRoom);
        btn.setText(testRoomName);
        ButtonViewAdapter testButton = new ButtonViewAdapter(m.getBaseContext(), rooms, roomNameView);

        assertEquals("Item inside adapter must be a button following layout suggestionRoom", testButton.getItem(1),btn);
        assertEquals("Button inside adapter must have text same with testRoomName",testButton.getItem(1).toString(),btn.getText());

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
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation()
                .addMonitor(MainActivity.class.getName(), null, false);

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
        getInstrumentation().sendStringSync("all");
        getInstrumentation().waitForIdleSync();

        //Click on the sendToReceiverButton to send the message to ReceiverActivity
        TouchUtils.clickView(this, joinButton);

        //Wait until all events from the MainHandler's queue are processed
        getInstrumentation().waitForIdleSync();

        //Wait until MainActivity was launched and get a reference to it.
        MainActivity mainActivity = (MainActivity) receiverActivityMonitor
                .waitForActivityWithTimeout(TIMEOUT_IN_MS);

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

        assertEquals("all", intent.getStringExtra(JoinActivity.ROOM_NAME));

        assertEquals("This is set correctly", "Room name: all", mainActivity.getTitle());

        //Unregister monitor for ReceiverActivity
        getInstrumentation().removeMonitor(receiverActivityMonitor);

    }
}
