package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import hk.ust.cse.hunkim.questionroom.db.DBUtil;
import hk.ust.cse.hunkim.questionroom.question.Thread;
/**
 * @author greg
 * @since 6/21/13
 * <p/>
 * This class is an example of how to use FirebaseListAdapter. It uses the <code>Chat</code> class to encapsulate the
 * data for each individual chat message
 */
public class ThreadListAdapter extends FirebaseListAdapter<Thread> {

    // The mUsername for this client. We use this to indicate which messages originated from this user
    private String roomName;
    private String key;
    MainActivity activity;

    public ThreadListAdapter(Query ref, Activity activity, int layout, String room_Name, String Key) {
        super(ref, Thread.class, layout, activity);
        roomName = room_Name;
        key = Key;
        // Must be MainActivity
        assert (activity instanceof MainActivity);

       this.activity = (MainActivity) activity;

    }

    /**
     * Bind an instance of the <code>Chat</code> class to our view. This method is called by <code>FirebaseListAdapter</code>
     * when there is a data change, and we are given an instance of a View that corresponds to the layout that we passed
     * to the constructor, as well as a single <code>Chat</code> instance that represents the current data to bind.
     *
     * @param view     A view instance corresponding to the layout we passed to the constructor.
     * @param thread An instance representing the current state of a chat message
     */
    @Override
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
                        m.CommentActivity((String) view.getTag());
                    }
                }
        );

        String msgString = "";


        //  escapeHTML for XSS protection
        msgString += "  <B>" + (thread.getAuthor()) + "</B> " +(thread.getThreadContent());

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

        ((TextView) view.findViewById(R.id.timestamp)).setText(relativeTime);



        view.setTag(thread.getKey());  // store key in the view
    }

    @Override
    protected void sortModels(List<Thread> mModels) {
        Collections.sort(mModels);
    }

    @Override
    protected void setKey(String key, Thread model) {
        model.setKey(key);
    }

    public Thread getItem(int i) {
        return mModels.get(i);
    }

    public void getComment(){
        mModels.clear();
    }

}

