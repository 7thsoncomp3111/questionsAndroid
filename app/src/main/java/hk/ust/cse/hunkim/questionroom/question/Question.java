package hk.ust.cse.hunkim.questionroom.question;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


import java.util.Date;

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
    //private String tags;
    private int upvote;
    private int order;
    private boolean newQuestion;

    public String getDateString() {
        return dateString;
    }

    private String dateString;

    public String getTrustedDesc() {
        return trustedDesc;
    }

    private String trustedDesc;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private Question() {
    }

    /**
     * Set question from a String message
     * @param message string message
     */
    public Question(String message) {
        this.wholeMsg = message;
        this.upvote = 0;
        this.head = getFirstSentence(message).trim();
        this.desc = "";
        if (this.head.length() < message.length()) {
            this.desc = message.substring(this.head.length());
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

    /*public String getTags() {
        return tags;
    }*/

    public int getOrder() {
        return order;
    }

    public boolean isNewQuestion() {
        return newQuestion;
    }

    public void updateNewQuestion() {
        newQuestion = this.timestamp > new Date().getTime() - 180000;
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
        // Push new on top
        other.updateNewQuestion(); // update NEW button
        this.updateNewQuestion();

        if (this.newQuestion != other.newQuestion) {
            return this.newQuestion ? 1 : -1; // this is the winner
        }


        if (this.upvote == other.upvote) {
            if (other.timestamp == this.timestamp) {
                return 0;
            }
            return other.timestamp > this.timestamp ? -1 : 1;
        }
        return this.upvote - other.upvote;
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
}
