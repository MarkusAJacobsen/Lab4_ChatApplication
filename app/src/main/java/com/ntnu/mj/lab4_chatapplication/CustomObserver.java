package com.ntnu.mj.lab4_chatapplication;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by markusja on 3/12/18.
 */

public class CustomObserver implements UpdateListener {
    ArrayList<DataModel> targets = new ArrayList<>();
    ArrayList<NotificationModel> notificationTargets = new ArrayList<>();

    public CustomObserver(NetworkClient model, DataModel target) {
        model.registerListener(this);
        targets.add(target);
    }

    public CustomObserver(NetworkClient model, NotificationModel target) {
        model.registerListener(this);
        notificationTargets.add(target);
    }

    @Override
    public void UpdateMessages(ArrayList<Message> newMessages) {
        for(DataModel target : targets) {
            target.setMessages(newMessages);
            Log.d("Updating class","");
        }
    }

    @Override
    public void UpdateUsers(ArrayList<User> newUsers) {
        for(DataModel target : targets) {
            target.setUsers(newUsers);
            Log.d("Updating class","");
        }
    }

    @Override
    public void NotifyMessageNotificationManager(Message newMessage) {
        for(NotificationModel target : notificationTargets) {
            target.newMessageReceived(newMessage);
        }
    }
}
