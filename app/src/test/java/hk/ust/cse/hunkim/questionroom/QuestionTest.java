package hk.ust.cse.hunkim.questionroom;

import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.TestCase;

import hk.ust.cse.hunkim.questionroom.question.Question;


/**
 * Created by hunkim on 7/15/15.
 */

/*public class QuestionTest  extends TestCase {
    Question q;



    protected void setUp() throws Exception {
        super.setUp();

        q = new Question("Hello? This is very nice",null);
    }

    @SmallTest

    public void testChatFirstString() {
        String[] strHead = {
                "Hello? This is very nice", "Hello?",
                "This is cool! Really?", "This is cool!",
                "How.about.this? Cool", "How.about.this?"
        };

        for (int i=0; i<strHead.length; i+=2) {
            String head = Question.getFirstSentence(strHead[i]);
            assertEquals("Chat.getFirstSentence", strHead[i+1], head);
        }
    }

    @SmallTest

    public void testHead() {
        assertEquals("Head", "Hello?", q.getHead());
    }

    public void testCompareto(){
        Question other = new Question("Hello?Hello2",null);
        Question other2 = new Question("Hello?Hello3",null);
        other.compareTo(other2);
    }
}*/
