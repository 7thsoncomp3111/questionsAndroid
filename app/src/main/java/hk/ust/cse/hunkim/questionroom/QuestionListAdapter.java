package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.text.format.DateUtils;
import android.widget.Toast;

import com.firebase.client.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import hk.ust.cse.hunkim.questionroom.db.DBUtil;
import hk.ust.cse.hunkim.questionroom.question.Question;
import hk.ust.cse.hunkim.questionroom.question.setImage;

/**
 * @author greg
 * @since 6/21/13
 * <p/>
 * This class is an example of how to use FirebaseListAdapter. It uses the <code>Chat</code> class to encapsulate the
 * data for each individual chat message
 */
public class QuestionListAdapter extends FirebaseListAdapter<Question> {

    // The mUsername for this client. We use this to indicate which messages originated from this user
    private String roomName;
    MainActivity activity;
    private String checkParse;
    private int sortType;
    private boolean sortNow;

    public QuestionListAdapter(Query ref, Activity activity, int layout, String room_Name,boolean sortNow, int sortType) {
        super(ref, Question.class, layout, activity);
        roomName = room_Name;
        this.sortNow = sortNow;
        this.sortType = sortType;
        // Must be MainActivity
        //assert (activity instanceof MainActivity);

       this.activity = (MainActivity) activity;
    }

    /**
     * Bind an instance of the <code>Chat</code> class to our view. This method is called by <code>FirebaseListAdapter</code>
     * when there is a data change, and we are given an instance of a View that corresponds to the layout that we passed
     * to the constructor, as well as a single <code>Chat</code> instance that represents the current data to bind.
     *
     * @param view     A view instance corresponding to the layout we passed to the constructor.
     * @param question An instance representing the current state of a chat message
     */
    @Override
    protected void populateView(View view, final Question question) {
        DBUtil dbUtil = activity.getDbutil();

        // Map a Chat object to an entry in our listview
        int echo = question.getUpvote();
        Button echoButton = (Button) view.findViewById(R.id.echo);
        echoButton.setText("" + echo);
        echoButton.setTextColor(Color.BLUE);

        echoButton.setTag(question.getKey()); // Set tag for button

        echoButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainActivity m = (MainActivity) view.getContext();
                        m.updateEcho((String) view.getTag());
                    }
                }

        );

        int downvote = question.getDownvote();
        Button downvoteButton = (Button) view.findViewById(R.id.minecho);
        downvoteButton.setText("" + downvote);
        downvoteButton.setTextColor(Color.BLUE);

        downvoteButton.setTag(question.getKey());

        downvoteButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainActivity m = (MainActivity) view.getContext();
                        m.updateDownvote((String) view.getTag());
                    }
                }

        );

        final Button moreButton = (Button) view.findViewById(R.id.button1);
        moreButton.setTag(question.getKey());
        moreButton.setOnClickListener(
                (new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        //Creating the instance of PopupMenu
                        PopupMenu popup = new PopupMenu(activity, moreButton);
                        //Inflating the Popup using xml file
                        popup.getMenuInflater()
                                .inflate(R.menu.popup_menu, popup.getMenu());

                        //registering popup with OnMenuItemClickListener
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                MainActivity m = (MainActivity) v.getContext();
                                if (item.getItemId() == R.id.one) {
                                    m.requestEmail("subscribe", (String) v.getTag());
                                }
                                else {
                                    m.requestEmail("unsubscribe", (String) v.getTag());
                                }
                                return true;
                            }
                        });

                        popup.show(); //showing popup menu
                    }
                })); //closing the setOnClickListener method

        Button CommentButton = (Button) view.findViewById(R.id.comment);
        CommentButton.setTag(question.getKey());
        CommentButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MainActivity m = (MainActivity) view.getContext();
                        String temp = question.getWholeMsg();
                        m.updateViews((String) view.getTag());
                        m.CommentActivity((String) view.getTag(), temp);
                    }
                }
        );

        String msgString = "";

        question.updateNewQuestion();
        if (question.isNewQuestion()) {
            msgString += "<font color=red>NEW </font>";
        }



        if(question.getImage() != null){

            ImageView picture = (ImageView) view.findViewById(R.id.img_desc);
            new setImage(picture).execute(question.getImage());

        } else {

        }

        msgString += "<B>" + Html.escapeHtml(question.getHead()) + "</B>" + Html.escapeHtml(question.getDesc());
        //  escapeHTML for XSS protection


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
        boolean clickable = (!dbUtil.contains(question.getKey()));

        echoButton.setClickable(clickable);
        echoButton.setEnabled(clickable);
        downvoteButton.setClickable(clickable);
        downvoteButton.setEnabled(clickable);
        view.setClickable(clickable);


        // http://stackoverflow.com/questions/8743120/how-to-grey-out-a-button
        // grey out our button
        if (!clickable) {
            echoButton.setCompoundDrawablesWithIntrinsicBounds( R.drawable.upvotefullcolor, 0, 0, 0);
            downvoteButton.setCompoundDrawablesWithIntrinsicBounds( R.drawable.downvotefullcolor, 0, 0, 0);
        }

        long now = System.currentTimeMillis();
        CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(question.getTimestamp(), now, DateUtils.MINUTE_IN_MILLIS);

        ((TextView) view.findViewById(R.id.timestamp)).setText(relativeTime);



        view.setTag(question.getKey());  // store key in the view
    }

    @Override
    protected void sortModels(List<Question> mModels) {

        if(sortNow) {
            List<Question> temp;
            if(sortType == 3) {
                int[] rating = new int[mModels.size()];
                temp = new ArrayList<Question>(mModels.size());
                for (int i = 0; i < mModels.size(); i++) {
                    temp.add(i, mModels.get(i));
                    rating[i] = mModels.get(i).getUpvote() - mModels.get(i).getDownvote();;
                }

                for (int i = 0; i < mModels.size(); i++) {
                    for (int j = 0; j <  mModels.size() - 1; j++){
                        if (rating[j + 1] < rating[j]){
                            int temp12 = rating[j];
                            Question temp22 = temp.remove(j + 1);
                            rating[j] = rating[j + 1];
                            temp.add(j,temp22);
                            rating[j + 1] = temp12;
                        }
                    }
                }

                Collections.reverse(temp);

            } else {

                temp = new ArrayList<Question>(mModels.size());
                for (int i = 0; i < mModels.size(); i++) {
                    temp.add(i, mModels.get(i));
                    Log.v("testete", temp.get(i).getWholeMsg());
                }

            }

            sortNow = false;

            int locationNormal = 0;
            int locationPinned = 0;

            mModels.clear();

            for (int i = 0; i < temp.size(); i++) {

                Question temp2 = temp.get(i);
                if (temp2.getPinned()) {
                    mModels.add(locationPinned, temp2);
                    locationPinned += 1;
                    locationNormal += 1;
                } else {
                    mModels.add(locationNormal, temp2);
                    locationNormal += 1;
                }
            }



            if(Arrays.asList(0, 2, 500).contains(sortType)){
                List<Question> listForDate1 = new ArrayList<Question>(locationPinned);
                List<Question> listForDate2 = new ArrayList<Question>(mModels.size() - locationPinned);

                for (int i = 0; i < locationPinned; i++) {
                    Question temp2 = mModels.get(i);
                    listForDate1.add(i, temp2);
                }
                for (int i = 0; i < mModels.size() - locationPinned; i++) {
                    Question temp2 = mModels.get(locationPinned + i);
                    listForDate2.add(i, temp2);
                }

                mModels.clear();
                if(listForDate1.size() != 0 && sortType != 0) {
                    Collections.reverse(listForDate1);
                }
                if(listForDate2.size() != 0 && sortType != 0) {
                    Collections.reverse(listForDate2);
                }
                mModels.addAll(listForDate1);
                mModels.addAll(listForDate2);
                //Collections.reverse(mModels);
            }

        }

    }


    @Override
    protected void setKey(String key, Question model) {
        model.setKey(key);
    }

    public Question getItem(int i) {
        return mModels.get(i);
    }

}

