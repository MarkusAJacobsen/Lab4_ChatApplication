package com.ntnu.mj.lab4_chatapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by markusja on 3/12/18.
 */

public class UserMessagesListActivity extends AppCompatActivity implements DataModel {
    private User user;
    private ArrayAdapter adapter;
    private ListView userMessageList;
    private ArrayList<Message> userMessages = new ArrayList<>();
    private NetworkClient client;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_message_list);


        userMessageList = findViewById(R.id.userMessages);
        adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, userMessages);
        userMessageList.setAdapter(adapter);

        String username = getIntent().getStringExtra("name");
        user = new User(username, "");

        client = new NetworkClient();
        client.registerListener(new CustomObserver(client, UserMessagesListActivity.this));
        client.triggerListeners();
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(UserMessagesListActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void setMessages(List<Message> messages) {
        syncUser((ArrayList<Message>) messages);
    }

    @Override
    public void setUsers(List<User> users) {}

    private void syncUser(ArrayList<Message> messages) {
        userMessages.clear();
        for(Message msg : messages){
            if(msg.getUsername().equals(this.user.getName())) {
                userMessages.add(msg);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
