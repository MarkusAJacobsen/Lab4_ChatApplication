package com.ntnu.mj.lab4_chatapplication;

import java.util.ArrayList;

/**
 * Created by markusja on 3/12/18.
 */

public interface UpdateListener {
    void UpdateMessages(ArrayList<Message> newMessages);
    void UpdateUsers(ArrayList<User> newUsers);
}
