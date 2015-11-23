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

import hk.ust.cse.hunkim.questionroom.question.*;
import hk.ust.cse.hunkim.questionroom.question.Thread;

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

        Question other = new Question("Hello?Hello1",null);
        Question other2 = new Question("Hello?Hello2",null);
        Question other4 = new Question("Hello?Hello4","http://www.dummytest.com/");
        other.compareTo(other);
        other.compareTo(other2);
        other4.plusUpvote();
        other.compareTo(other4);
        try{
            java.lang.Thread.sleep(30000);

        } catch (Exception e){
            e.printStackTrace();
        }

        Question other3 = new Question("HEllo??Hello3",null);
        other.compareTo(other3);
        Object ee = "testJUnits";
        other.equals(ee);
        try{
            java.lang.Thread.sleep(1000);

        } catch (Exception e){
            e.printStackTrace();
        }
        Question other5 = new Question("HEllo?Hello5",null);
        other5.compareTo(other3);
        other3.compareTo(other5);
        other5.getFirstSentence("hellohello");
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

        assertEquals(true, mainActivity.getGalPicker());


    }

    public void testThread(){
        Thread other = new Thread("Hello?Hello1","hey");
        Thread other2 = new Thread("Hello?Hello2","hey");
        Thread other4 = new Thread("Hello?Hello4","heyo");
        other.compareTo(other);
        other.compareTo(other2);
        //other4.plusUpvote();
        other.compareTo(other4);
        try{
            java.lang.Thread.sleep(30000);

        } catch (Exception e){
            e.printStackTrace();
        }

        Thread other3 = new Thread("HEllo??Hello3",null);
        other.compareTo(other3);
        Object ee = "testJUnits";
        other.equals(ee);
        try{
            java.lang.Thread.sleep(1000);

        } catch (Exception e){
            e.printStackTrace();
        }
        Thread other5 = new Thread("HEllo?Hello5",null);
        other5.compareTo(other3);
        other3.compareTo(other5);

    }
    @MediumTest
    public void testPostingMessage2() {

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
            java.lang.Thread.currentThread().sleep(5000);
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
                text.requestFocus();
            }
        });


        text.setText("This is test2! <h1>big</h1>");
        getInstrumentation().waitForIdleSync();
        mButton.performClick();
        /*getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                text.requestFocus();
            }
        });
        text.setText("<img test");
        getInstrumentation().waitForIdleSync();
        mButton.performClick();*/
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
            java.lang.Thread.currentThread().sleep(5000);
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
            java.lang.Thread.currentThread().sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                text.requestFocus();
            }
        });
        text.setText("");
        getInstrumentation().waitForIdleSync();
        mButton.performClick();

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                text.requestFocus();
            }
        });
        text.setText("This is test! <h1>big</h1>");
        mainActivity.setUploadedPirctureLink("http://www.comp3111.xyz/_/rsrc/1440401874035/team/543142_10151358767209521_1148078012_n.jpg?height=200&width=200");
        mButton.performClick();

        // wait for the new row to be handled by Firebase and added to the list view
        try {
            java.lang.Thread.currentThread().sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ListView testListView = mainActivity.getListView();
        mainActivity.sortQuestions(0,testListView);
        mainActivity.sortQuestions(1,testListView);
        mainActivity.sortQuestions(2, testListView);
        mainActivity.sortQuestions(3, testListView);

        mainActivity.updateEcho("-K3nOPyGaxaC6zrAR5uY");
        mainActivity.updateDownvote("-K3nOPyGaxaC6zrAR5uY");
        mainActivity.updateEcho("AIJGAERIJGAERG");
        mainActivity.updateDownvote("AIGJAEIRGJAER");


        //assertEquals("Child count: ", originalCount + 1, lView.getCount());

    }

    public void testOnActivityResult() {
        // Get current Activity and check initial status:
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation().addMonitor(MainActivity.class.getName(), null, false);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                startActivity(mStartIntent, null, null);
            }
        });
        MainActivity myActivity = (MainActivity) receiverActivityMonitor.waitForActivityWithTimeout(1000);

        // Mock up an ActivityResult:
        Intent returnIntent = new Intent();

        Instrumentation.ActivityResult activityResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, returnIntent);

        // Create an ActivityMonitor that catch ChildActivity and return mock ActivityResult:
        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI.getClass().getName(), activityResult , true);

        // Simulate a button click that start ChildActivity for result:
        final ImageButton button = (ImageButton) myActivity.findViewById(R.id.uploadImage);
        myActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // click button and open next activity.
                button.performClick();
            }
        });

        // Wait for the ActivityMonitor to be hit, Instrumentation will then return the mock ActivityResult:
        //Activity mainTest = getInstrumentation().waitForMonitorWithTimeout(activityMonitor, 5);
        Intent data = new Intent();
        myActivity.onActivityResult(1,Activity.RESULT_OK,data);
        myActivity.onActivityResult(10,Activity.RESULT_OK,data);
        myActivity.onActivityResult(1,Activity.RESULT_CANCELED,data);
        myActivity.onActivityResult(10, Activity.RESULT_CANCELED, data);

        final EditText inputText = (EditText) myActivity.findViewById(R.id.messageInput);
        myActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // click button and open next activity.
                inputText.requestFocus();
            }
        });
        inputText.performClick();
        getInstrumentation().waitForIdleSync();


    }

    public void testCommentActivity(){
        Instrumentation.ActivityMonitor receiverActivityMonitor = getInstrumentation().addMonitor(MainActivity.class.getName(), null, false);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                startActivity(mStartIntent, null, null);
            }
        });

        MainActivity myActivity = (MainActivity) receiverActivityMonitor.waitForActivityWithTimeout(1000);
        myActivity.CommentActivity("test","test");
    }


}
