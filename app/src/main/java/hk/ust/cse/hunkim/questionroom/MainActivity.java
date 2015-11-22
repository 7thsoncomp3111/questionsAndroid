package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import hk.ust.cse.hunkim.questionroom.db.DBHelper;
import hk.ust.cse.hunkim.questionroom.db.DBUtil;
import hk.ust.cse.hunkim.questionroom.question.Question;
import hk.ust.cse.hunkim.questionroom.question.uploadPicture;

public class MainActivity extends ListActivity {

    // TODO: change this to your own Firebase URL
    private static final String FIREBASE_URL = "https://resplendent-inferno-9346.firebaseio.com/";

    private String roomName;
    private Firebase mFirebaseRef;
    private ValueEventListener mConnectedListener;
    private QuestionListAdapter mChatListAdapter;
    private static final int GET_FROM_GALLERY = 1;
    private String uploadedPirctureLink = "";
    private boolean havePicture = false;
    private DBUtil dbutil;

    public DBUtil getDbutil() {
        return dbutil;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialized once with an Android context.
        Firebase.setAndroidContext(this);

        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        assert (intent != null);

        // Make it a bit more reliable
        roomName = intent.getStringExtra(JoinActivity.ROOM_NAME);
        if (roomName == null || roomName.length() == 0) {
            roomName = "all";
        }

        setTitle("Room name: " + roomName);

        // Setup our Firebase mFirebaseRef
        mFirebaseRef = new Firebase(FIREBASE_URL).child("room").child(roomName).child("questions");

        // Edit by Erez, give a header title for each room

        TextView p = (TextView) findViewById(R.id.roomname_View);

        if(roomName.length() > 10){
            String q = roomName.substring(0,10) + "...";
            p.setText(q);
        } else {
            p.setText(roomName);
        }
        //p.setText(roomName);

        //////////////////////////////////////
        // Added for Sorting (Below this line)
        Spinner staticSpinner = (Spinner) findViewById(R.id.static_spinner);

        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(this, R.array.sorting_array,
                        android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        staticAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        staticSpinner.setAdapter(staticAdapter);

        Spinner dynamicSpinner = (Spinner) findViewById(R.id.dynamic_spinner);

        String[] items = new String[] { "Title", "Activity", "Date", "Rating" };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, items);

        dynamicSpinner.setAdapter(adapter);

        dynamicSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));
                final ListView listView = (ListView)findViewById(android.R.id.list);

                listView.setAdapter(null);

                //Will choose Title at first
                if(position==0)
                {
                    sortByTitle(listView);
                }
                else if (position==1)
                {
                    //TODO change to sortByActivity()
                    sortByUpvote(listView);
                }
                else if (position==2)
                {
                    sortByTime(listView);
                }
                else
                {
                    sortByRating(listView);
                }
                listView.setAdapter(mChatListAdapter);

                mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
                    @Override
                    public void onChanged() {
                        super.onChanged();
                        listView.setSelection(mChatListAdapter.getCount()-1);
                    }
                });
                mChatListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO SORTING IMPLEMENTATION
            }
        });
        // End of the line for Sorting
        ////////////////////////////////

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

        findViewById(R.id.uploadImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText inputText = (EditText) findViewById(R.id.messageInput);
                String input = inputText.getText().toString();
                if(input.equals("")) {
                    Toast.makeText(MainActivity.this, "Please input the message first!", Toast.LENGTH_SHORT).show();
                } else {
                    startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
                }
            }
        });

        // get the DB Helper
        DBHelper mDbHelper = new DBHelper(this);
        dbutil = new DBUtil(mDbHelper);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                String filePath = getPath(selectedImage);
                new uploadPicture(this).execute(filePath);

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }
    }



    public String getPath(Uri uri) {
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        String imagePath = cursor.getString(column_index);

        return cursor.getString(column_index);
    }

    public void setUploadedPirctureLink(String msg){
        uploadedPirctureLink = msg;
        havePicture = true;
        sendMessage();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Finally, a little indication of connection status
        mConnectedListener = mFirebaseRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    Toast.makeText(MainActivity.this, "Connected to Firebase", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Disconnected from Firebase", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        mChatListAdapter.cleanup();
    }

    private void sendMessage() {
        EditText inputText = (EditText) findViewById(R.id.messageInput);
        String input = inputText.getText().toString();
        if (!input.equals("")) {
            Question question;
            if(havePicture) {
                // Concat the actual string with the image upload link
                question = new Question(input,uploadedPirctureLink);
            } else {
                if(input.contains("<img")){
                    Toast.makeText(MainActivity.this, "Please don't input img tag on the message", Toast.LENGTH_SHORT).show();
                    return;
                }
                question = new Question(input,null);
            }
            // Create our 'model', a Chat object

            // Create a new, auto-generated child of that chat location, and save our chat data there
            mFirebaseRef.push().setValue(question);
            inputText.setText("");
            uploadedPirctureLink = "";
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

        final Firebase orderRef = mFirebaseRef.child(key).child("order");
        orderRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long orderValue = (Long) dataSnapshot.getValue();
                        Log.e("Order update:", "" + orderValue);

                        orderRef.setValue(orderValue - 1);
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

        final Firebase orderRef = mFirebaseRef.child(key).child("order");
        orderRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long orderValue = (Long) dataSnapshot.getValue();
                        Log.e("Order update:", "" + orderValue);

                        orderRef.setValue(orderValue + 1);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );

        // Update SQLite DB
        dbutil.put(key);
    }

    public void sortByUpvote(ListView listView){
        mChatListAdapter = new QuestionListAdapter(
                mFirebaseRef.orderByChild("upvote").limitToFirst(200),
                this, R.layout.question, roomName);
    }

    public void sortByTitle(ListView listView){
        mChatListAdapter = new QuestionListAdapter(
                mFirebaseRef.orderByChild("head").limitToFirst(200),
                this, R.layout.question, roomName);
    }

    public void sortByRating(ListView listView){
        mChatListAdapter = new QuestionListAdapter(
                mFirebaseRef.orderByChild("upvotePercent").limitToFirst(200),
                this, R.layout.question, roomName);
    }

    public void sortByTime(ListView listView){
        mChatListAdapter = new QuestionListAdapter(
                mFirebaseRef.orderByChild("timestamp").limitToFirst(200),
                this, R.layout.question, roomName);
    }

    public void Close(View view) {
        finish();
    }
}
