package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.TouchUtils;
import android.test.suitebuilder.annotation.MediumTest;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import hk.ust.cse.hunkim.questionroom.question.Question;

/**
 * Created by hunkim on 7/20/15.
 */
public class CommentActivityTest extends ActivityUnitTestCase<CommentActivity> {

    private Intent mStartIntent;
    private ImageButton mButton;
    private TextView Question;
    private ListView list;

    public CommentActivityTest() {
        super(CommentActivity.class);
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
        mStartIntent.putExtra(JoinActivity.ROOM_NAME, "androidcommenttest");
        mStartIntent.putExtra("Key", "-K3oXT8fDl33NrVfNtv6");
        mStartIntent.putExtra("Question", "test");
    }


    public void testPreconditions() {
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation().addMonitor(CommentActivity.class.getName(), null, false);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                startActivity(mStartIntent, null, null);
            }
        });
        CommentActivity commentActivity = (CommentActivity) receiverActivityMonitor.waitForActivityWithTimeout(1000);
        mButton = (ImageButton) commentActivity.findViewById(R.id.sendButton);
        Question = (TextView) commentActivity.findViewById(R.id.Question);
        list = (ListView)commentActivity.findViewById(android.R.id.list);
        assertNotNull(getActivity());
        assertNotNull(mButton);
        assertNotNull(Question);
        assertNotNull(list);

    }


    @MediumTest
    public void testDisplay() {

        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation().addMonitor(CommentActivity.class.getName(), null, false);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                startActivity(mStartIntent, null, null);
            }
        });
        CommentActivity commentActivity = (CommentActivity) receiverActivityMonitor.waitForActivityWithTimeout(1000);

        mButton = (ImageButton) commentActivity.findViewById(R.id.sendButton);
        final TextView text = (TextView) commentActivity.findViewById(R.id.Question);
        final ListView lView = commentActivity.getListView();
        final EditText editText = (EditText)commentActivity.findViewById(R.id.messageInput);

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

        assertEquals("Question ", "Question: test", text.getText());

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                editText.requestFocus();
            }
        });

        getInstrumentation().waitForIdleSync();

        editText.setText("This is test to reply test");
        mButton.performClick();

        // wait for the new row to be handled by Firebase and added to the list view
        try {
            Thread.currentThread().sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals("Child count: ", lView.getCount(), originalCount + 1);
        View view = commentActivity.mChatListAdapter.getView(0, null, null);
        assertNotNull(view);
        Button downvoteButton = (Button) view.findViewById(R.id.minecho);
        Button echoButton = (Button) view.findViewById(R.id.echo);
        Button CommentButton = (Button) view.findViewById(R.id.comment);
        assertNotNull(downvoteButton);
        assertNotNull(echoButton);
        assertNotNull(CommentButton);
        downvoteButton.performClick();
        echoButton.performClick();
        CommentButton.performClick();
        hk.ust.cse.hunkim.questionroom.question.Thread test1 = commentActivity.mChatListAdapter.getItem(0);
        hk.ust.cse.hunkim.questionroom.question.Thread test2 = commentActivity.mChatListAdapter.getItem("-K3oobxNrXA4F6Y0r5uZ");
        assertEquals(commentActivity.mChatListAdapter.isSecondLvReply(test1), false);
        assertEquals(commentActivity.mChatListAdapter.isSecondLvReply(commentActivity.mChatListAdapter.getItem("-K3omSyuq7GudrBA8k51")), true);
        assertEquals(commentActivity.mChatListAdapter.isSecondLvReply(commentActivity.mChatListAdapter.getItem("-K3omTxCTjtIjz-uRNJW")), false);

        commentActivity.mChatListAdapter.cleanup();
    }
}
