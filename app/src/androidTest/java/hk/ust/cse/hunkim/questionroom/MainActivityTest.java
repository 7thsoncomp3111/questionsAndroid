package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.text.Html;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by hunkim on 7/20/15.
 */
public class MainActivityTest extends ActivityUnitTestCase<MainActivity> {

    private Intent mStartIntent;
    private ImageButton mButton;
    private Button vButton;
    private TextView resultView;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // In setUp, you can create any shared test data,
        // or set up mock components to inject
        // into your Activity. But do not call startActivity()
        // until the actual test methods.
        // into your Activity. But do not call startActivity()
        // until the actual test methods.
        mStartIntent = new Intent(Intent.ACTION_MAIN);
        mStartIntent.putExtra(JoinActivity.ROOM_NAME, "all");
    }

    @MediumTest
    public void testPreconditions() {
        startActivity(mStartIntent, null, null);
        mButton = (ImageButton) getActivity().findViewById(R.id.sendButton);
        resultView = (TextView) getActivity().findViewById(R.id.head_desc);
        assertNotNull(getActivity());
        assertNotNull(mButton);

        assertEquals("This is set correctly", "Room name: all", getActivity().getTitle());
    }


    @MediumTest
    public void testPostingMessage() {
        Activity activity = startActivity(mStartIntent, null, null);
        mButton = (ImageButton) activity.findViewById(R.id.sendButton);
        final TextView text = (TextView) activity.findViewById(R.id.messageInput);
        final ListView lView = getActivity().getListView();

        getInstrumentation().callActivityOnStart(getActivity());

        // wait for the data to be loaded by Firebase
        try {
            Thread.currentThread().sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int originalCount = lView.getCount();

        assertNotNull(mButton);
        assertNotNull(text);
        assertNotNull(lView);

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                lView.performItemClick(lView, 0, lView.getItemIdAtPosition(0));
            }
        });
        getInstrumentation().waitForIdleSync();

        try {
            Thread.currentThread().sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                text.requestFocus();
            }
        });

        getInstrumentation().waitForIdleSync();

        text.setText("This is test! <h1>big</h1>");
        mButton.performClick();

        // wait for the new row to be handled by Firebase and added to the list view
        try {
            Thread.currentThread().sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals("Child count: ", originalCount + 1, lView.getCount());

    }

    public void testUpvoteFeature() {
        Button upvoteButton;
        Activity activity = startActivity(mStartIntent, null, null);
        final ListView lView = getActivity().getListView();
        assertNotNull(lView);

        getInstrumentation().callActivityOnStart(getActivity());

// wait for the data to be loaded by Firebase
        try {
            Thread.currentThread().sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        upvoteButton = (Button) lView.getChildAt(0).findViewById(R.id.echo);
        assertNotNull(upvoteButton);

        int initialUpvote = Integer.parseInt(upvoteButton.getText().toString());
        boolean initialClickable = upvoteButton.isClickable();

        getInstrumentation().waitForIdleSync();

        upvoteButton.performClick();

        try {
            Thread.currentThread().sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(initialClickable)
        {
            int postUpvote = Integer.parseInt(upvoteButton.getText().toString());
            assertEquals("Upvote should increase by one", initialUpvote+1, postUpvote);
            assertEquals("Upvote button.clickable should be false", false, upvoteButton.isClickable());
        }
        else
        {
            int postUpvote = Integer.parseInt(upvoteButton.getText().toString());
            assertEquals("Upvote should not increased by one", initialUpvote, postUpvote);
        }
    }

    public void testDownvoteFeature() {
        Button downvoteButton;
        Activity activity = startActivity(mStartIntent, null, null);
        final ListView lView = getActivity().getListView();
        assertNotNull(lView);

        getInstrumentation().callActivityOnStart(getActivity());

// wait for the data to be loaded by Firebase
        try {
            Thread.currentThread().sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        downvoteButton = (Button) lView.getChildAt(0).findViewById(R.id.minecho);
        assertNotNull(downvoteButton);

        int initialDownvote = Integer.parseInt(downvoteButton.getText().toString());
        boolean initialClickable = downvoteButton.isClickable();

        getInstrumentation().waitForIdleSync();

        downvoteButton.performClick();

        try {
            Thread.currentThread().sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(initialClickable)
        {
            int postUpvote = Integer.parseInt(downvoteButton.getText().toString());
            assertEquals("Downvote should increase by one", initialDownvote+1, postUpvote);
            assertEquals("Downvote button.clickable should be false", false, downvoteButton.isClickable());
        }
        else
        {
            int postUpvote = Integer.parseInt(downvoteButton.getText().toString());
            assertEquals("Downvote should not increased by one", initialDownvote, postUpvote);
        }
    }
}
