package com.ntnu.mj.lab4_chatapplication;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by markusja on 3/9/18.
 */

/**
 * User model
 */
public class User {
    private String name;
    private String userId;
    private List<Message> messages;

    public User(){}

    public User(String name, String userId) {
        this.name = name;
        this.userId = userId;
        messages = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Exclude
    public List<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }

    @Override
    public String toString(){
        return this.name;
    }
}
