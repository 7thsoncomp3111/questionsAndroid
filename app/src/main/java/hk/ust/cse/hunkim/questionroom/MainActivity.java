package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import hk.ust.cse.hunkim.questionroom.db.DBHelper;
import hk.ust.cse.hunkim.questionroom.db.DBUtil;
import hk.ust.cse.hunkim.questionroom.question.Question;
import hk.ust.cse.hunkim.questionroom.question.Subscription;
import hk.ust.cse.hunkim.questionroom.question.uploadPicture;

public class MainActivity extends ListActivity {

    // TODO: change this to your own Firebase URL
    private static final String FIREBASE_URL = "https://resplendent-inferno-9346.firebaseio.com/";
    private String roomName;
    private Firebase mFirebaseRef;
    private Firebase subscriptionRef;
    private ValueEventListener mConnectedListener;
    private QuestionListAdapter mChatListAdapter;
    private static final int GET_FROM_GALLERY = 1;
    private String uploadedPirctureLink = "";
    private boolean havePicture = false;
    private DBUtil dbutil;
    public static MainActivity activity ;
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
        activity = this;
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
        subscriptionRef = new Firebase(FIREBASE_URL).child("subscription");

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
        Spinner dynamicSpinner = (Spinner) findViewById(R.id.dynamic_spinner);

        String[] items = new String[] { "Title", "Activity", "Date", "Rating" };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, R.id.spinner_title, items);

        dynamicSpinner.setAdapter(adapter);

        dynamicSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));
                final ListView listView = (ListView)findViewById(android.R.id.list);

                listView.setAdapter(null);

                //Will choose Title at first
                sortQuestions(position, listView);

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
                    setPickerStart();
                    startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
                }
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

    public String getRoomName(){return roomName;}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {

            try {
                Uri selectedImage = data.getData();
                Bitmap bitmap = null;
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                String filePath = getPath(selectedImage);
                new uploadPicture(this).execute(filePath);

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e){
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
        mChatListAdapter = new QuestionListAdapter(
                mFirebaseRef.orderByChild("title").limitToFirst(200),
                this, R.layout.question, roomName,false,500);
        listView.setAdapter(mChatListAdapter);

        mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mChatListAdapter.getCount() - 1);
            }
        });

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

    public EditText forTestGetMessageInput(){
        return (EditText) findViewById(R.id.messageInput);
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
        //Infeasible. If true, button is already disabled in QuestionListAdapter.java
        /*
        if (dbutil.contains(key)) {
            Log.e("Dupkey", "Key is already in the DB!");
            return;
        }
        */
        final Firebase upvoteRef = mFirebaseRef.child(key).child("upvote");
        upvoteRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long upvoteValue = (Long) dataSnapshot.getValue();
                        Log.e("Upvote update:", "" + upvoteValue);
                        try {
                            upvoteRef.setValue(upvoteValue + 1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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

                        try {
                            orderRef.setValue(orderValue - 1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

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
        //Infeasible. If true, button is already disabled in QuestionListAdapter.java
        /*
        if (dbutil.contains(key)) {
            Log.e("Dupkey", "Key is already in the DB!");
            return;
        }
        */
        final Firebase downvoteRef = mFirebaseRef.child(key).child("downvote");
        downvoteRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long downvoteValue = (Long) dataSnapshot.getValue();
                        Log.e("Downvote update:", "" + downvoteValue);

                        try {
                            downvoteRef.setValue(downvoteValue + 1);
                        } catch (Exception e){
                            e.printStackTrace();
                        }

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


                        try {
                            orderRef.setValue(orderValue + 1);
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );

        // Update SQLite DB
        dbutil.put(key);
    }


    public void sortQuestions(int position, ListView listView){
        if(position==0)
        {
            mChatListAdapter = new QuestionListAdapter(
                    mFirebaseRef.orderByChild("head").limitToFirst(200),
                    this, R.layout.question, roomName,true,position);
        }
        else if (position==1)
        {
            //TODO change to sortByActivity()
            mChatListAdapter = new QuestionListAdapter(
                    mFirebaseRef.orderByChild("upvote").limitToFirst(200),
                    this, R.layout.question, roomName,false,position);
            Collections.sort(mChatListAdapter.mModels);
        }
        else if (position==2)
        {
            mChatListAdapter = new QuestionListAdapter(
                    mFirebaseRef.orderByChild("timestamp").limitToFirst(200),
                    this, R.layout.question, roomName,true,position);
        }
        else
        {
            mChatListAdapter = new QuestionListAdapter(
                    mFirebaseRef.orderByChild("upvotePercent").limitToFirst(200),
                    this, R.layout.question, roomName,true,position);
        }
    }

    public void updateViews(String key) {
        final Firebase viewsRef = mFirebaseRef.child(key).child("views");
        viewsRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long viewsValue = (Long) dataSnapshot.getValue();
                        Log.e("views update:", "" + viewsValue);

                        viewsRef.setValue(viewsValue + 1);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                }
        );

    }

    public void CommentActivity(String key, String question) {
        Intent intent = new Intent(this, CommentActivity.class);
        intent.putExtra(JoinActivity.ROOM_NAME, roomName);
        intent.putExtra("Key", key);
        intent.putExtra("Question", question);
        startActivity(intent);
    }

    public void requestEmail(String choice, String key) {
        final Context context = this;
        final String mChoice = choice;
        final String mKey = key;
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.prompts, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, final int id) {
                final String emailAdress = userInput.getText().toString();
                if (!emailAdress.equals("")) {
                    if(mChoice.equals("subscribe")) {
                        Subscription subscription = new Subscription(emailAdress, mKey);

                        // Create a new, auto-generated child of that chat location, and save our chat data there
                        subscriptionRef.push().setValue(subscription);
                        Toast.makeText(MainActivity.this, "Subscription recorded!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        subscriptionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                for(DataSnapshot child : snapshot.getChildren()){
                                    String childEmail = (String)child.child("email").getValue();
                                    String childId = (String) child.child("id").getValue();
                                    if(childEmail==emailAdress && childId==mKey);
                                    {
                                        //subscriptionRef.child(child.getKey()).removeValue();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                            }
                        });
                    }
                }
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void Close(View view) {
        finish();
    }


}
