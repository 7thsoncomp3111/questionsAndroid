package hk.ust.cse.hunkim.questionroom.question;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import hk.ust.cse.hunkim.questionroom.MainActivity;

/**
 * Created by hunkim on 7/16/15.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Question implements Comparable<Question> {

    /**
     * Must be synced with firebase JSON structure
     * Each must have getters
     */

    public String key;
    private String wholeMsg;
    private String head;
    private String headLastChar;
    private String desc;
    private String linkedDesc;
    private boolean completed;
    private long timestamp;
    private String image;
    private int upvote;
    private int views;
    private float upvotePercent;
    private int downvote;
    private float downvotePercent;
    private int order;
    private boolean newQuestion;
    private int view;



    public String getDateString() {
        return dateString;
    }

    private String dateString;

    public String getTrustedDesc() {
        return trustedDesc;
    }

    private String trustedDesc;

    public int getView(){
        return view;
    }

    public String getImage(){ return image; }
    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private Question() {
    }

    /**
     * Set question from a String message
     * @param message string message
     */
    public Question(String message,String imageLink) {
        this.wholeMsg = message;
        this.upvote = 0;
        this.downvote = 0;
        this.views = 0;
        this.upvotePercent = 0;
        this.downvotePercent = 0;
        this.order = 0;
        this.head = getFirstSentence(message).trim();
        this.desc = "";
        if (this.head.length() < message.length()) {
            this.desc = message.substring(this.head.length());
        }
        if(imageLink == null){
            this.image = null;
        } else {
            this.image = imageLink;
        }
        // get the last char
        this.headLastChar = head.substring(head.length() - 1);

        this.timestamp = new Date().getTime();
    }

    /**
     * Get first sentence from a message
     * @param message
     * @return
     */
    public static String getFirstSentence(String message) {
        String[] tokens = {". ", "? ", "! "};

        int index = -1;

        for (String token : tokens) {
            int i = message.indexOf(token);
            if (i == -1) {
                continue;
            }

            if (index == -1) {
                index = i;
            } else {
                index = Math.min(i, index);
            }
        }

        if (index == -1) {
            return message;
        }

        return message.substring(0, index+1);
    }

    /* -------------------- Getters ------------------- */
    public String getHead() {
        return head;
    }

    public String getDesc() {
        return desc;
    }

    public int getUpvote() {
        return upvote;
    }
    public void plusUpvote() { this.upvote += 1;}

    public int getDownvote() {
        return downvote;
    }

    public int getViews() {
        return views;
    }


    public float getUpvotePercent() {
        return upvotePercent;
    }

    public float getDownvotePercent() {
        return downvotePercent;
    }

    public String getWholeMsg() {
        return wholeMsg;
    }

    public String getHeadLastChar() {
        return headLastChar;
    }

    public String getLinkedDesc() {
        return linkedDesc;
    }

    public boolean isCompleted() {
        return completed;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getOrder() {
        return order;
    }

    public boolean isNewQuestion() {
        return newQuestion;
    }

    public void updateNewQuestion() {
        newQuestion = this.timestamp > new Date().getTime() - 30000;
    }
    @JsonIgnore

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /**
     * New one/high upvote goes bottom
     * @param other other chat
     * @return order
     */
    @Override
    public int compareTo(Question other) {
        double thisActivity = this.views/2+(this.upvote+this.downvote)*1.5+this.returnThreadNo()*2;
        double otherActivity = other.views/2+(other.upvote+other.downvote)*1.5+other.returnThreadNo()*2;


        if (thisActivity == otherActivity)
                return 0;

        return thisActivity > otherActivity ? 1 : -1;


    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Question)) {
            return false;
        }
        Question other = (Question)o;
        return key.equals(other.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    public int returnThreadNo(){
        final int[] counter = {0};
        String FIREBASE_URL = "https://resplendent-inferno-9346.firebaseio.com/";
        MainActivity activity;
        Firebase mFirebaseRef = new Firebase(FIREBASE_URL).child("room").child(MainActivity.activity.getRoomName()).child("threads");
        final List<Thread> mModels = new ArrayList<Thread>();
        mFirebaseRef.orderByChild("prev").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    if (postSnapshot.getValue(Thread.class).getPrev().equals(getKey())){
                        mModels.add(postSnapshot.getValue(Thread.class));
                        continue;
                    }
                    for (int i = 0; i < mModels.size(); i++)
                        if (postSnapshot.getValue(Thread.class).getPrev().equals(mModels.get(i).getKey())){
                            mModels.add(postSnapshot.getValue(Thread.class));
                            continue;
                        }
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
        return mModels.size();
        }
}

