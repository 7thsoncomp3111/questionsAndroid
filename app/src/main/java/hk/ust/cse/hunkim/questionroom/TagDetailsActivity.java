package hk.ust.cse.hunkim.questionroom;

/**
 * Created by samch on 31/10/2015.
 */
import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import hk.ust.cse.hunkim.questionroom.question.Question;

public class TagDetailsActivity extends Activity {

    private ArrayList<String> messagesWithTag = new ArrayList<String>();
    private static final String FIREBASE_URL = "https://resplendent-inferno-9346.firebaseio.com/";
    private String roomName;
    private Firebase mFirebaseRef;
    private ListView messagesListView;
    private ArrayAdapter<String> adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_details);
        messagesWithTag.clear();
        messagesListView = (ListView) findViewById(R.id.messagesWithTag);
        adapter = new ArrayAdapter<String>(this, (android.R.layout.simple_list_item_1), messagesWithTag);
        messagesListView.setAdapter(adapter);
    }

    public void onStart(){
        super.onStart();
        messagesWithTag.clear();
        adapter.notifyDataSetChanged();
        Firebase.setAndroidContext(this);
        //Get the content URI
        Uri uri = getIntent().getData();
        //strip off hashtag from the URI
        final String tag=uri.toString().split("/")[4];
        roomName = uri.toString().split("/")[3];
        if (roomName == null || roomName.length() == 0) {
            roomName = "all";
        }
        mFirebaseRef = new Firebase(FIREBASE_URL).child("room").child(roomName).child("questions");



        mFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                messagesWithTag.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren())
                    if (postSnapshot.getValue(Question.class).getWholeMsg().contains(tag))
                        messagesWithTag.add(postSnapshot.getValue(Question.class).getWholeMsg());

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        adapter.notifyDataSetChanged();

    }

}