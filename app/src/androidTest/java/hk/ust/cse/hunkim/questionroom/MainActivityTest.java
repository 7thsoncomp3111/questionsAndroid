package hk.ust.cse.hunkim.questionroom;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.TouchUtils;
import android.test.suitebuilder.annotation.MediumTest;
import android.text.Html;
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
        mStartIntent.putExtra(JoinActivity.ROOM_NAME, "ANDROID");
    }

    @MediumTest
    public void testQuestionCompareto(){
        Question other = new Question("Hello?Hello2",null);
        Question other2 = new Question("Hello?Hello3",null);
        Question other4 = new Question("Hello?Hello3",null);
        other.compareTo(other2);
        other4.plusUpvote();
        other.compareTo(other4);
        try{
            Thread.sleep(30000);
        } catch (Exception e){
            e.printStackTrace();
        }
        Question other3 = new Question("HEllo??Hello4",null);
        other.compareTo(other3);
        Object ee = "testJUnits";
        other.equals(ee);
    }

    public void testPreconditions() {
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation().addMonitor(MainActivity.class.getName(), null, false);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                startActivity(mStartIntent, null, null);
            }
        });
        MainActivity mainActivity = (MainActivity) receiverActivityMonitor.waitForActivityWithTimeout(1000);
        mButton = (ImageButton) mainActivity.findViewById(R.id.sendButton);
        resultView = (TextView) mainActivity.findViewById(R.id.head_desc);
        assertNotNull(getActivity());
        assertNotNull(mButton);

        assertEquals("This is set correctly", "Room name: ANDROID", mainActivity.getTitle());
    }

    public void testStartPicturePicker(){
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation().addMonitor(MainActivity.class.getName(), null, false);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                startActivity(mStartIntent, null, null);
            }
        });
        MainActivity mainActivity = (MainActivity) receiverActivityMonitor.waitForActivityWithTimeout(1000);
        EditText inputText = (EditText) mainActivity.findViewById(R.id.messageInput);
        ImageButton p = (ImageButton) mainActivity.findViewById(R.id.uploadImage);
        String input = inputText.getText().toString();


        inputText.setText("testJUnitSubmit");
        p.performClick();

        assertEquals(true,mainActivity.getGalPicker());


    }

    @MediumTest
    public void testPostingMessage() {

        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation().addMonitor(MainActivity.class.getName(), null, false);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                startActivity(mStartIntent, null, null);
            }
        });
        MainActivity mainActivity = (MainActivity) receiverActivityMonitor.waitForActivityWithTimeout(1000);

        mButton = (ImageButton) mainActivity.findViewById(R.id.sendButton);
        final TextView text = (TextView) mainActivity.findViewById(R.id.messageInput);
        final ListView lView = mainActivity.getListView();

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

        //assertEquals("Child count: ", originalCount + 1, lView.getCount());

    }
}
