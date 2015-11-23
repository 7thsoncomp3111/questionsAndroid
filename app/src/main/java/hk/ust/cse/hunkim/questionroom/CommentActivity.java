package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.IOException;

import hk.ust.cse.hunkim.questionroom.db.DBHelper;
import hk.ust.cse.hunkim.questionroom.db.DBUtil;
import hk.ust.cse.hunkim.questionroom.question.Thread;
import hk.ust.cse.hunkim.questionroom.question.uploadPicture;

public class CommentActivity extends MainActivity {

    private static final String FIREBASE_URL = "https://resplendent-inferno-9346.firebaseio.com/";
    private String roomName;
    private String key;
    private String question;
    private Firebase mFirebaseRef;
    public ThreadListAdapter mChatListAdapter;
    private static final int GET_FROM_GALLERY = 1;
    private DBUtil dbutil;
    private String uploadedPirctureLink = "";
    private boolean havePicture = false;


    //Add following for testing
    private boolean galPicker = false;

    public DBUtil getDbutil() {
        return dbutil;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initialized once with an Android context.
        Firebase.setAndroidContext(this);

        setContentView(R.layout.activity_comment);

        Intent intent = getIntent();
        assert (intent != null);

        // Make it a bit more reliable
        roomName = intent.getStringExtra(JoinActivity.ROOM_NAME);
        key = intent.getStringExtra("Key");
        question = intent.getStringExtra("Question");
        if (roomName == null || roomName.length() == 0) {
            roomName = "all";
        }


        // Setup our Firebase mFirebaseRef
        mFirebaseRef = new Firebase(FIREBASE_URL).child("room").child(roomName).child("threads");
        TextView p = (TextView) findViewById(R.id.roomname_View);
        p.setText("Comments");

        // Add listener to 'completed' value. Only admin can modify the value.
        Firebase tempFirebaseRef = new Firebase(FIREBASE_URL).child("room").child(roomName).child("questions");
        final ImageButton sendButton = (ImageButton) findViewById(R.id.sendButton);

        final Firebase completedRef = tempFirebaseRef.child(key).child("completed");
        completedRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean completed = (boolean) dataSnapshot.getValue();
                        sendButton.setClickable(!completed);

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );
        // Setup our input methods. Enter key on the keyboard or pushing the send button
        EditText inputText = (EditText) findViewById(R.id.messageInput);
        inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMessage();
                }
                return true;
            }
        });

        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });


        // get the DB Helper
        DBHelper mDbHelper = new DBHelper(this);
        dbutil = new DBUtil(mDbHelper);
    }


    @Override
    public void onStart() {
        super.onStart();

        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
        final ListView listView = (ListView)findViewById(android.R.id.list);
        // Tell our list adapter that we only want 200 messages at a time
        mChatListAdapter = new ThreadListAdapter(
                mFirebaseRef.orderByChild("prev").limitToFirst(200),
                this, R.layout.question, roomName, key);
        mChatListAdapter.notifyDataSetChanged();
        listView.setAdapter(mChatListAdapter);

        TextView q = (TextView) findViewById(R.id.Question);
        q.setText("Question: " +question);

        mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mChatListAdapter.getCount() - 1);
            }
        });


    }

    @Override
    public void onStop() {
        super.onStop();
        mChatListAdapter.cleanup();
    }

    private void sendMessage() {
        EditText inputText = (EditText) findViewById(R.id.messageInput);
        String input = inputText.getText().toString();
        if (!input.equals("")) {
            // Create our 'model', a Chat object
            Thread thread = new Thread(input, key);
            // Create a new, auto-generated child of that chat location, and save our chat data there
            mFirebaseRef.push().setValue(thread);
            inputText.setText("");
        }
    }

    public void updateEcho(String key) {
        if (dbutil.contains(key)) {
            Log.e("Dupkey", "Key is already in the DB!");
            return;
        }

        final Firebase upvoteRef = mFirebaseRef.child(key).child("upvote");
        upvoteRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long upvoteValue = (Long) dataSnapshot.getValue();
                        Log.e("Upvote update:", "" + upvoteValue);

                        upvoteRef.setValue(upvoteValue + 1);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );


        // Update SQLite DB
        dbutil.put(key);
    }

    public void updateDownvote(String key) {
        if (dbutil.contains(key)) {
            Log.e("Dupkey", "Key is already in the DB!");
            return;
        }

        final Firebase downvoteRef = mFirebaseRef.child(key).child("downvote");
        downvoteRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long downvoteValue = (Long) dataSnapshot.getValue();
                        Log.e("Downvote update:", "" + downvoteValue);

                        downvoteRef.setValue(downvoteValue + 1);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );



        // Update SQLite DB
        dbutil.put(key);
    }

    public void CommentActivity(String keyReceived, String question) {
        key = keyReceived;
    }

    public void Close(View view) {
        finish();
    }
}
