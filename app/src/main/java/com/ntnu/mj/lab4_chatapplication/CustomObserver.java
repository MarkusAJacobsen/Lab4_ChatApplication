package com.ntnu.mj.lab4_chatapplication;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by markusja on 3/12/18.
 */

/**
 * Observer, keeps a list of targets to trigger when invoked
 */
public class CustomObserver implements UpdateListener {
    ArrayList<DataModel> targets = new ArrayList<>();
    ArrayList<NotificationModel> notificationTargets = new ArrayList<>();

    /**
     * Constructor
     * @param model NetworkClient to register the listener with
     * @param target DataModel Target to trigger
     */
    public CustomObserver(NetworkClient model, DataModel target) {
        model.registerListener(this);
        targets.add(target);
    }

    /**
     * Constructor
     * @param model NetworkClient to register the listener with
     * @param target NotificationModel Target to trigger
     */
    public CustomObserver(NetworkClient model, NotificationModel target) {
        model.registerListener(this);
        notificationTargets.add(target);
    }

    /**
     * Update messages with every registered target
     * @param newMessages ArrayList<Message>
     */
    @Override
    public void UpdateMessages(ArrayList<Message> newMessages) {
        for(DataModel target : targets) {
            target.setMessages(newMessages);
            Log.d("Updating class","");
        }
    }

    /**
     * Update users with every registered target
     * @param newUsers ArrayList<Users>
     */
    @Override
    public void UpdateUsers(ArrayList<User> newUsers) {
        for(DataModel target : targets) {
            target.setUsers(newUsers);
            Log.d("Updating class","");
        }
    }

    /**
     * Notify about a new message with every NotificationModel target
     * @param newMessage Message
     */
    @Override
    public void NotifyMessageNotificationManager(Message newMessage) {
        for(NotificationModel target : notificationTargets) {
            target.newMessageReceived(newMessage);
        }
    }
}
