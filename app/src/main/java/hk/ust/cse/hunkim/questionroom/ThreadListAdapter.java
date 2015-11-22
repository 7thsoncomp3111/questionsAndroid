package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import hk.ust.cse.hunkim.questionroom.db.DBUtil;
import hk.ust.cse.hunkim.questionroom.question.*;
import hk.ust.cse.hunkim.questionroom.question.Thread;

import static android.widget.LinearLayout.*;

/**
 * @param <Thread> The class type to use as a model for the data contained in the children of the given Firebase location
 * @author greg
 * @since 6/21/13
 * <p/>
 * This class is a generic way of backing an Android ListView with a Firebase location.
 * It handles all of the child events at the given Firebase location. It marshals received data into the given
 * class type. Extend this class and provide an implementation of <code>populateView</code>, which will be given an
 * instance of your list item mLayout and an instance your class that holds your data. Simply populate the view however
 * you like and this class will handle updating the list as the data changes.
 */
public class ThreadListAdapter extends BaseAdapter {

    private Query mRef;
    private Class<Thread> mModelClass;
    private int mLayout;
    private LayoutInflater mInflater;
    public List<Thread> mModels;
    private Map<String, Thread> mModelKeys;
    private ChildEventListener mListener;
    private String roomName;
    private String key;
    MainActivity activity;


    public ThreadListAdapter(Query mRef, Activity activity, int mLayout, String room_Name, String Key) {
        this.mRef = mRef;
        this.mModelClass = Thread.class;
        this.mLayout = mLayout;
        mInflater = activity.getLayoutInflater();
        mModels = new ArrayList<Thread>();
        mModelKeys = new HashMap<String, Thread>();
        roomName = room_Name;
        key = Key;
        assert (activity instanceof MainActivity);

        this.activity = (MainActivity) activity;
        // Look for all child events. We will then map them to our own internal ArrayList, which backs ListView
        mListener = this.mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                Thread model = dataSnapshot.getValue(ThreadListAdapter.this.mModelClass);
                if (!vaildReply(model)) return; // skip the replies that belongs to other questions
                String modelName = dataSnapshot.getKey();
                mModelKeys.put(modelName, model);

                // TOFIX: Any easy way to ser key?
                setKey(modelName, model);

                // bug seeded
                // mModels.add(-1, model);

                // Insert into the correct location, based on previousChildName
                if (previousChildName == null) {
                    mModels.add(0, model);
                } else {
                   if (model.getPrev().equals(key))
                       mModels.add(model);
                    else {
                       for (int i = 0; i < mModels.size(); i ++)
                           if (model.getPrev().equals(mModels.get(i).getKey())){
                               mModels.add(i+1, model);
                               break;
                           }

                   }
                }
                for (int i = 0; i < mModels.size(); i ++)
                    Log.e("Content", "" +mModels.get(i).getThreadContent());
                notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                // One of the mModels changed. Replace it in our list and name mapping
                String modelName = dataSnapshot.getKey();
                Thread oldModel = mModelKeys.get(modelName);
                Thread newModel = dataSnapshot.getValue(ThreadListAdapter.this.mModelClass);

                // TOFIX: Any easy way to ser key?
                setKey(modelName, newModel);


                int index = mModels.indexOf(oldModel);
                mModels.set(index, newModel);


                // update map
                mModelKeys.put(modelName, newModel);

                notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                // A model was removed from the list. Remove it from our list and the name mapping
                String modelName = dataSnapshot.getKey();
                Thread oldModel = mModelKeys.get(modelName);
                mModels.remove(oldModel);
                mModelKeys.remove(modelName);
                notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

                // A model changed position in the list. Update our list accordingly
                String modelName = dataSnapshot.getKey();
                Thread oldModel = mModelKeys.get(modelName);
                Thread newModel = dataSnapshot.getValue(ThreadListAdapter.this.mModelClass);

                // TOFIX: Any easy way to ser key?
                setKey(modelName, newModel);

                int index = mModels.indexOf(oldModel);
                mModels.remove(index);
                if (previousChildName == null) {
                    mModels.add(0, newModel);
                } else {
                    Thread previousModel = mModelKeys.get(previousChildName);
                    int previousIndex = mModels.indexOf(previousModel);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == mModels.size()) {
                        mModels.add(newModel);
                    } else {
                        mModels.add(nextIndex, newModel);
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("ThreadListAdapter", "Listen was cancelled, no more updates will occur");
            }

        });
    }

    public void cleanup() {
        // We're being destroyed, let go of our mListener and forget about all of the mModels
        mRef.removeEventListener(mListener);
        mModels.clear();
        mModelKeys.clear();
    }


    @Override
    public int getCount() {
        return mModels.size();
    }


    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = mInflater.inflate(mLayout, viewGroup, false);
        }


        // Let's get keys and models
        Thread model = mModels.get(i);

        // Call out to subclass to marshall this model into the provided view
        populateView(view, model);
        return view;
    }

    /**
     * Each time the data at the given Firebase location changes, this method will be called for each item that needs
     * to be displayed. The arguments correspond to the mLayout and mModelClass given to the constructor of this class.
     * <p/>
     * Your implementation should populate the view using the data contained in the model.
     *
     * @param v     The view to populate
     * @param model The object containing the data used to populate the view
     */

    protected void populateView(View view, Thread thread) {
        DBUtil dbUtil = activity.getDbutil();

        // Map a Chat object to an entry in our listview
        int echo = thread.getUpvote();
        Button echoButton = (Button) view.findViewById(R.id.echo);
        echoButton.setText("" + echo);
        echoButton.setTextColor(Color.BLUE);

        echoButton.setTag(thread.getKey()); // Set tag for button

        echoButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainActivity m = (MainActivity) view.getContext();
                        m.updateEcho((String) view.getTag());
                    }
                }

        );

        int downvote = thread.getDownvote();
        Button downvoteButton = (Button) view.findViewById(R.id.minecho);
        downvoteButton.setText("" + downvote);
        downvoteButton.setTextColor(Color.BLUE);

        downvoteButton.setTag(thread.getKey());

        downvoteButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainActivity m = (MainActivity) view.getContext();
                        m.updateDownvote((String) view.getTag());
                    }
                }

        );

        Button CommentButton = (Button) view.findViewById(R.id.comment);
        CommentButton.setTag(thread.getKey());
        CommentButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainActivity m = (MainActivity) view.getContext();
                        m.CommentActivity((String) view.getTag(),"");
                    }
                }
        );


        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(30, 0, 0, 0);

        String msgString = "";
        String indent = "  ";
        if (!(thread.getPrev().equals(key))){ // If it is first lv
            if (isSecondLvReply(thread))
                echoButton.setLayoutParams(layoutParams);
            else {//If it is 3rd or higher level
                layoutParams.setMargins(60, 0, 0, 0);
                echoButton.setLayoutParams(layoutParams);
            }
        }

        //  escapeHTML for XSS protection
        msgString += indent+"<B>" + (thread.getAuthor()) + "</B> " +(thread.getThreadContent());

        ((TextView) view.findViewById(R.id.head_desc)).setText(Html.fromHtml(msgString));
        //Pattern to find if there's a hash tag in the message
        //i.e. any word starting with a # and containing letter or numbers or _
        Pattern tagMatcher = Pattern.compile("[#]+[A-Za-z0-9-_]+\\b");

        //Scheme for Linkify, when a word matched tagMatcher pattern,
        //the room name and that word is appended to this URL and used as content URI
        String newActivityURL = "content://hk.ust.cse.hunkim.questionroom.tagdetailsactivity/"+roomName+"/";

        //Attach Linkify to TextView
        Linkify.addLinks(((TextView) view.findViewById(R.id.head_desc)), tagMatcher, newActivityURL);

        /*view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        MainActivity m = (MainActivity) view.getContext();
                                        m.updateEcho((String) view.getTag());
                                    }
                                }

        );*/

        // check if we already clicked
        boolean clickable = !dbUtil.contains(thread.getKey());

        echoButton.setClickable(clickable);
        echoButton.setEnabled(clickable);
        downvoteButton.setClickable(clickable);
        downvoteButton.setEnabled(clickable);
        view.setClickable(clickable);


        // http://stackoverflow.com/questions/8743120/how-to-grey-out-a-button
        // grey out our button
        if (clickable) {
            echoButton.getBackground().setColorFilter(null);
            downvoteButton.getBackground().setColorFilter(null);
        } else {
            echoButton.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
            downvoteButton.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
        }
        long now = System.currentTimeMillis();
        CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(thread.getTimestamp(), now, DateUtils.MINUTE_IN_MILLIS);

        ((TextView) view.findViewById(R.id.timestamp)).setText(indent+relativeTime);



        view.setTag(thread.getKey());  // store key in the view
    }

    protected void sortModels(List<Thread> mModels) {
        Collections.sort(mModels);
    }

    protected void setKey(String key, Thread model) {
        model.setKey(key);
    }

    public Thread getItem(int i) {
        return mModels.get(i);
    }

    public Thread getItem(String key) {
        for (int i = 0; i < mModels.size(); i++)
            if (key.equals(mModels.get(i).getKey()))
                return mModels.get(i);
        return null;
    }

    public boolean vaildReply(Thread model){
        if (model.getPrev().equals(key))
            return true;
        for (int i = 0; i < mModels.size(); i++)
            if (model.getPrev().equals(mModels.get(i).getKey()))
                return true;
        return false;
    }

    public boolean isSecondLvReply(Thread model){
        if ((model.getPrev().equals(key))) return false;
        if (getItem(model.getPrev()).getPrev().equals(key))
            return true;
        return false;
    }

}
