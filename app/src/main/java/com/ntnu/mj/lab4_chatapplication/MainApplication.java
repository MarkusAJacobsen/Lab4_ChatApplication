package com.ntnu.mj.lab4_chatapplication;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by markusja on 3/12/18.
 */

/**
 * If user is not present in SharedPreferences, launch Login,
 * else launch MainActivity
 */
public class MainApplication extends Application {
    @Override
    public void onCreate(){
        if(!getUserFromPreference()) {
            startLoginActivity();
        }
        super.onCreate();

    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    private boolean getUserFromPreference() {
        final SharedPreferences preferences = getSharedPreferences(SharedPreferencesStatics.PREFS_FILE, MODE_PRIVATE);

        final String username = preferences.getString(SharedPreferencesStatics.FIELD_USERNAME, "");
        final String uid = preferences.getString(SharedPreferencesStatics.FIELD_UID, "");

        if(username != "" && uid != "") {
            return true;
        }

        return false;
    }
}
