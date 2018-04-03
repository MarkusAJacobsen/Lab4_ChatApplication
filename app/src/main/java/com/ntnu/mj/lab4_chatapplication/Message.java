package com.ntnu.mj.lab4_chatapplication;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by markusja on 3/9/18.
 */

/**
 * Model for a message
 */
public class Message {
    private String id;
    private String timestamp;
    private String username;
    private String description;

    /**
     * Empty constructor
     */
    public Message(){}

    /**
     * New message constructor
     * @param username String
     * @param description String
     */
    public Message(String username, String description) {
        this.timestamp = createTimestamp();
        this.username = username;
        this.description = description;
    }

    /**
     * New Message constructor
     * @param id String
     * @param timestamp String
     * @param username String
     * @param description String
     */
    public Message(String id, String timestamp, String username, String description) {
        this.id = id;
        this.timestamp = timestamp;
        this.username = username;
        this.description = description;
    }

    /////////////Setters and Getters///////////////////
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    //////////Setters and getters end//////////////

    /**
     * Create a timestamp
     * @return String
     */
    public String createTimestamp (){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return dateFormat.format(new Date()); // Find todays date
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    @Override
    public String toString(){
        return this.username + "\n" +
                this.description + "\n" +
                "Sent: " + this.timestamp;
    }


}
