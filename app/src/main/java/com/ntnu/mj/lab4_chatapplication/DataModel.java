package com.ntnu.mj.lab4_chatapplication;

import java.util.List;

/**
 * Created by markusja on 3/12/18.
 */

public interface DataModel {
    void setMessages(List<Message> messages);
    void setUsers(List<User> users);
}
