package com.ntnu.mj.lab4_chatapplication;

/**
 * Created by markusja on 4/1/18.
 */

/**
 * Interface containing method for when a newMessage is received
 */
public interface NotificationModel {
    void newMessageReceived(Message message);
}
