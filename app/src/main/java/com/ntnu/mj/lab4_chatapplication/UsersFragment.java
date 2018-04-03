package com.ntnu.mj.lab4_chatapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by markusja on 3/12/18.
 */

/**
 * Fragment listing all users from firestore
 */
public class UsersFragment extends android.support.v4.app.Fragment implements DataModel {
    private NetworkClient client;
    private MainActivity parent;
    private ArrayAdapter adapter;
    private ListView usersListView;
    private List<User> userList = new ArrayList<>();

    /**
     * Create and return View
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * @return View
     */
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View userView = inflater.inflate(R.layout.fragment_users_list, container, false);
        parent = (MainActivity) getActivity();
        client = parent.getClient();
        client.registerListener(new CustomObserver(client, this));

        usersListView = userView.findViewById(R.id.users);


        adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, userList);
        usersListView.setAdapter(adapter);
        usersListView.setOnItemClickListener(new UserClickListener());

        return userView;
    }

    /**
     * When reopened, trigger listeners with the client
     */
    @Override
    public void onResume(){
        super.onResume();
        client.triggerListeners();
    }

    @Override
    public void setMessages(List<Message> messages) {}

    @Override
    public void setUsers(List<User> users) {
        this.userList.clear();
        this.userList.addAll(users);
        this.adapter.notifyDataSetChanged();
    }

    /**
     * Custom OnItemClickListener. Opens the UserMessagesListActivity when a list item
     * is pressed
     */
    private class UserClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String username = userList.get(position).getName();
            Intent intent = new Intent(getContext(), UserMessagesListActivity.class);
            intent.putExtra("name", username);
            startActivity(intent);
        }
    }

}
