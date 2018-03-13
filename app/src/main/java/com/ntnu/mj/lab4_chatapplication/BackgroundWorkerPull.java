package com.ntnu.mj.lab4_chatapplication;

import android.os.AsyncTask;

import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by markusja on 3/9/18.
 */

public class BackgroundWorkerPull extends AsyncTask<String, String, Void> {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        @Override
        protected Void doInBackground(String... strings) {
            return null;
        }
}
