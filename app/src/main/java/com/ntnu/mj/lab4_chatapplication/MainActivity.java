package com.ntnu.mj.lab4_chatapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.R.layout.simple_list_item_1;

/**
 * Main activity holding to fragments in a tab view kinda way
 */
public class MainActivity extends FragmentActivity {
    private FragmentTabHost mTabHost;

    private User user = null;
    private NetworkClient client;

    /**
     * Set up GUI elements
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getUserFromPreference();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        client = new NetworkClient();

        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("Global").setIndicator("Global"),
                GlobalFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("Users").setIndicator("Users"),
                UsersFragment.class, null);

        setUpBackgroundWorker();

    }

    /**
     * Start the background service
     */
    private void setUpBackgroundWorker(){
        Intent notificationService = new Intent(getApplicationContext(), NotificationService.class);
        startService(notificationService);
    }


    private boolean getUserFromPreference() {
        final SharedPreferences preferences = getSharedPreferences(SharedPreferencesStatics.PREFS_FILE, MODE_PRIVATE);

        final String username = preferences.getString(SharedPreferencesStatics.FIELD_USERNAME, "");
        final String uid = preferences.getString(SharedPreferencesStatics.FIELD_UID, "");

        if(username != "" && uid != "") {
            user = new User(username, uid);
            return true;
        }

        return false;
    }

    public User getUser(){
        return this.user;
    }

    public NetworkClient getClient() {
        return client;
    }
}
