package hk.ust.cse.hunkim.questionroom;

/**
 * Created by samch on 31/10/2015.
 */
import java.util.ArrayList;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

public class TagDetailsActivity extends Activity {

    //List to store messages that contain clicked Tag.
    private ArrayList<String> messagesWithTag;
    private static final String FIREBASE_URL = "https://resplendent-inferno-9346.firebaseio.com/";
    private String roomName;
    private Firebase mFirebaseRef;
    private QuestionListAdapter mChatListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this.getApplication());

        Intent intent = getIntent();
        assert (intent != null);

        // Make it a bit more reliable
        roomName = intent.getStringExtra(JoinActivity.ROOM_NAME);
        if (roomName == null || roomName.length() == 0) {
            roomName = "all";
        }
        mFirebaseRef = new Firebase(FIREBASE_URL).child("room").child(roomName).child("questions");
        mChatListAdapter = new QuestionListAdapter(mFirebaseRef.orderByKey().limitToFirst(200), this, R.layout.question, roomName);
        messagesWithTag=new ArrayList<String>();
        ListView messagesListView=(ListView)findViewById(R.id.messagesWithTag);


        //Get the content URI
        Uri uri = getIntent().getData();
        //strip off hashtag from the URI
        String tag=uri.toString().split("/")[3];


        messagesWithTag = mChatListAdapter.GetAllTaggedQuestions(tag);
        //Show the list of messages which has the hashtag
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, messagesWithTag);
        messagesListView.setAdapter(adapter);

    }

}