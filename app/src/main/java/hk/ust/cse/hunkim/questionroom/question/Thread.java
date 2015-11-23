package hk.ust.cse.hunkim.questionroom.question;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


import java.util.Date;

/**
 * Created by hunkim on 7/16/15.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Thread implements Comparable<Thread> {

    /**
     * Must be synced with firebase JSON structure
     * Each must have getters
     */

    public String key;
    public String author;
    public String content;
    public int downvote;
    public String prev;
    private long timestamp;
    private int upvote;


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
    private Thread() {
    }

    /**
     * Set question from a String message
     * @param message string message
     */
    public Thread(String message, String key) {
        this.upvote = 0;
        this.downvote = 0;
        this.prev = key;
        this.content = message;
        this.timestamp = new Date().getTime();
    }


    /* -------------------- Getters ------------------- */


    public int getUpvote() {
        return upvote;
    }

    public int getDownvote() {
        return downvote;
    }

    public String getAuthor() {
        return author;
    }

    public String getThreadContent() {
        return content;
    }

    public String getPrev() {
        return prev;
    }


    public long getTimestamp() {
        return timestamp;
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
    public int compareTo(Thread other) {



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
        if (!(o instanceof Thread)) {
            return false;
        }
        Thread other = (Thread)o;
        return key.equals(other.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
