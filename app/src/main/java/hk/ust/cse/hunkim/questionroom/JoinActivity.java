package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import hk.ust.cse.hunkim.questionroom.question.ButtonViewAdapter;


/**
 * A login screen that offers login via email/password.
 */
public class JoinActivity extends Activity {
    public static String ROOM_NAME = "Room_name";
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    // UI references.
    private TextView roomNameView;

    private boolean forTest = false;


    // Edit Rey Add main page suggestion rooms

    private static final String FIREBASE_URL = "https://resplendent-inferno-9346.firebaseio.com/";

    private Firebase mFirebaseRef;

    private ArrayList<String> rooms = new ArrayList<String>();

    private GridView List2view;
    private ButtonViewAdapter btn2;

    // End of Edit

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);


        // Set up the login form.
        roomNameView = (TextView) findViewById(R.id.room_name);

        roomNameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    attemptJoin(textView);
                }
                return true;
            }
        });
        //Added to retrive firebase data


        //rooms.clear();
        List2view = (GridView) findViewById(R.id.List2View);
        //btn2 = new ButtonViewAdapter(this, rooms, roomNameView);
        //List2view.setAdapter(btn2);

    }

    public void onStart(){
        super.onStart();

        rooms.clear();
        btn2 = new ButtonViewAdapter(this, rooms, roomNameView);
        List2view.setAdapter(btn2);
        btn2.notifyDataSetChanged();

        Firebase.setAndroidContext(this);
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

        btn2.notifyDataSetChanged();


    }

    public void setForTest(){
        if(forTest) {
            forTest = false;
        } else {
            forTest = true;
        }
    }
    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptJoin(View view) {
        // Reset errors.
        roomNameView.setError(null);

        // Store values at the time of the login attempt.
        String room_name = roomNameView.getText().toString();

        boolean cancel = false;

        // Check for a valid email address.
        if (TextUtils.isEmpty(room_name)) {
            roomNameView.setError(getString(R.string.error_field_required));

            cancel = true;
        } else if (!isEmailValid(room_name)) {
            roomNameView.setError(getString(R.string.error_invalid_room_name));
            cancel = true;
        }

        if (cancel) {
            roomNameView.setText("");
            roomNameView.requestFocus();
        } else {
            // Start main activity
            Intent intent = new Intent(this, MainActivity.class);
            if(forTest){

            } else {
                intent.putExtra(ROOM_NAME, room_name);
            }
            startActivity(intent);
        }
    }

    private boolean isEmailValid(String room_name) {
        // http://stackoverflow.com/questions/8248277
        // Make sure alphanumeric characters
        return !room_name.matches("^.*[^a-zA-Z0-9 ].*$");
    }

    public ArrayList<String> getRooms(){
        return rooms;
    }
}


