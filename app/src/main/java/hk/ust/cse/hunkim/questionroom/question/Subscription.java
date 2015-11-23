package hk.ust.cse.hunkim.questionroom.question;

import java.util.Date;

/**
 * Created by felycia on 24/11/2015.
 */
public class Subscription {
    public String email;
    public String id;
    public String key;

    private Subscription() {
    }

    /**
     * Set question from a String message
     * @param email string message
     */
    public Subscription(String email, String questionKey) {
        this.email = email;
        this.id = questionKey;
    }

    public String getEmail() {return email;};
    public String getId() {return id;};
    public String getKey() {return key;};

}
