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

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.jar.Manifest;

import hk.ust.cse.hunkim.questionroom.db.DBHelper;
import hk.ust.cse.hunkim.questionroom.db.DBUtil;
import hk.ust.cse.hunkim.questionroom.question.Question;
import hk.ust.cse.hunkim.questionroom.question.uploadPicture;

public class CommentActivity extends MainActivity {

    // TODO: change this to your own Firebase URL
    private static final String FIREBASE_URL = "https://resplendent-inferno-9346.firebaseio.com/";
    private String roomName;
    private String key;
    private String question;
    private Firebase mFirebaseRef;
    private ThreadListAdapter commentListAdapter;
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

        //

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
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });

        // get the DB Helper
        DBHelper mDbHelper = new DBHelper(this);
        dbutil = new DBUtil(mDbHelper);
    }

    public void setPickerStart(){
        this.galPicker = true;
    }

    public boolean getGalPicker(){
        return this.galPicker;
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

    public String getUploadedPirctureLink(){
        return uploadedPirctureLink;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
        final ListView listView = (ListView)findViewById(android.R.id.list);
        // Tell our list adapter that we only want 200 messages at a time
        commentListAdapter = new ThreadListAdapter(
                mFirebaseRef.orderByChild("prev").limitToFirst(200),
                this, R.layout.question, roomName, key);
        commentListAdapter.notifyDataSetChanged();
        listView.setAdapter(commentListAdapter);

        TextView q = (TextView) findViewById(R.id.Question);
        q.setText("Question: " +question);

        commentListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(commentListAdapter.getCount() - 1);
            }
        });


    }

    @Override
    public void onStop() {
        super.onStop();
        commentListAdapter.cleanup();
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
                    Toast.makeText(CommentActivity.this, "Please don't input img tag on the message", Toast.LENGTH_SHORT).show();
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
