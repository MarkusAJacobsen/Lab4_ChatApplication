package com.ntnu.mj.lab4_chatapplication;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

/**
 * Created by markusja on 4/2/18.
 */

/**
 * https://it-ride.blogspot.no/2010/10/android-implementing-notification.html
 */
public class NotificationService extends Service {
    private NetworkClient client;
    private PowerManager.WakeLock mWakeLock;
    private String userName;
    private static boolean isRunning = false;
    private static final String NOTIFICATION_TITLE = "New message received";

    @Override
    public void onCreate(){
        Toast.makeText(this, "Service started", Toast.LENGTH_LONG).show();
        getLoggedInUser();
        isRunning = true;
    }

    @Override
    public void onDestroy(){
        isRunning = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * This is called on 2.0+ (API level 5 or higher). Returning
     * START_NOT_STICKY tells the system to not restart the service if it is
     * killed because of poor resource (memory/cpu) conditions.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleIntent(intent);
        return START_NOT_STICKY;
    }

    private void getLoggedInUser(){
        SharedPreferences sp = getSharedPreferences("Preferences", MODE_PRIVATE);
        userName = sp.getString(SharedPreferencesStatics.FIELD_USERNAME, null);
    }

    private void handleIntent(Intent intent) {
        // obtain the wake lock
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG); mWakeLock.acquire();
        // check the global background data setting
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (!cm.getBackgroundDataSetting()) {
            stopSelf();
            return;
        }

        // do the actual work, in a separate thread
        new PollTask().execute();
    }


    /**
     * https://www.tutorialspoint.com/android/android_notifications.htm
     * https://developer.android.com/reference/android/app/NotificationManager.html
     * @param message
     */
    private void createNotification(Message message){
        NotificationManager notiManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);

        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(this).setContentTitle(NOTIFICATION_TITLE)
                .setContentText(message.getDescription())
                .setContentTitle(message.getUsername())
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(resultPendingIntent)
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notiManager.notify(0, notification);
    }

    public static boolean isRunning() {
        return isRunning;
    }

    private class PollTask extends AsyncTask<Void, Void, Void> {
        private boolean newMessage = false;
        private FirebaseFirestore db = FirebaseFirestore.getInstance();
        private List<Message> resultCache = new ArrayList<>();
        private static final String FIELD_TIMESTAMP = "timestamp";

        @Override
        protected Void doInBackground(Void... voids) {
            fetchMessages();
            createTimedChecker();
            return null;
        }

        private void createTimedChecker(){
            Timer timer = new Timer();

            TimerTask doAsync = new TimerTask() {
                @Override
                public void run() {
                    check();
                }
            };

            timer.scheduleAtFixedRate(doAsync, 30000, 30000);
        }

        private void check() {
            if(newMessage) {
                createNotification(getLatestMessage());
                newMessage = false;
            }
        }

        private void fetchMessages(){
            db.collection("messages")
                    .orderBy(FIELD_TIMESTAMP, Query.Direction.DESCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w(TAG, "Listen failed.", e);
                                return;
                            }
                            ArrayList<Message> localCache;
                            localCache = (ArrayList<Message>) getMessages();
                            for(DocumentChange dc : value.getDocumentChanges()) {
                                switch (dc.getType()) {
                                    case ADDED:
                                        for (DocumentSnapshot doc : value) {
                                            boolean hit = false;
                                            for (Message cached : localCache) {
                                                if (doc.getId() == cached.getId()) {
                                                    hit = true;
                                                }
                                            }

                                            if(!hit) {
                                                Message msg = new Message(doc.getId(), doc.getString("timestamp"), doc.getString("username"), doc.getString("description"));
                                                localCache.add(msg);
                                                if(!Objects.equals(userName, msg.getUsername())) {
                                                    newMessage = true;
                                                }
                                            }
                                        }
                                        break;
                                    case REMOVED: break;
                                }
                            }

                            Collections.sort(localCache, new Comparator<Message>() {
                                @Override
                                public int compare(Message message, Message t1) {
                                    return t1.getTimestamp().compareTo(message.getTimestamp());
                                }
                            });

                            setMessages(localCache);
                        }
                    });
        }

        private Message getLatestMessage() {
            return resultCache.get(0);
        }

        private List<Message> getMessages(){
            return this.resultCache;
        }

        private void setMessages(ArrayList<Message> newMessages) {
            this.resultCache = newMessages;
        }
    }
}
