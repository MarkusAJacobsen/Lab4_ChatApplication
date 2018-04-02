package com.ntnu.mj.lab4_chatapplication;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.R.layout.simple_list_item_1;

/**
 * Created by markusja on 3/12/18.
 */

public class GlobalFragment extends android.support.v4.app.Fragment implements DataModel {
    private Button mSend;
    private TextView mMessage;
    private ListView mGlobalChat;
    private FragmentTabHost mTabHost;
    private MainActivity parent;

    private User user;
    private List<Message> messages;
    private ArrayAdapter adapter;
    private NetworkClient client;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View globalView = inflater.inflate(R.layout.fragment_global_chat, container, false);

        mSend = globalView.findViewById(R.id.send);
        mMessage = globalView.findViewById(R.id.description);
        mMessage.setSelected(false);
        mGlobalChat = globalView.findViewById(R.id.globalFeed);

        messages = new ArrayList<>();
        populateChat();

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = String.valueOf(mMessage.getText());

                mMessage.setText("");

                Message msg = new Message(user.getName(), text);
                messages.add(msg);
                String messageId = client.publishMessage(msg);
                messages.remove(msg);
                msg.setId(messageId);
                messages.add(msg);

                user.addMessage(msg);
            }
        });

        return globalView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        parent = (MainActivity) getActivity();
        user = parent.getUser();
        client = parent.getClient();
        client.registerListener(new CustomObserver(client, this));
    }


    @Override
    public void onResume(){
        super.onResume();
        client.triggerListeners();
    }

    private void syncUser() {
        for(Message msg : messages){
            if(msg.getUsername().equals(user.getName())) {
                user.addMessage(msg);
            }
        }
    }

    private void populateChat() {
        adapter = new ArrayAdapter(getContext(), simple_list_item_1, messages);
        mGlobalChat.setAdapter(adapter);
    }

    @Override
    public void setMessages(List<Message> messages) {
        this.messages.clear();
        this.messages.addAll(messages);
        this.adapter.notifyDataSetChanged();
        syncUser();
    }

    @Override
    public void setUsers(List<User> users) {}
}
