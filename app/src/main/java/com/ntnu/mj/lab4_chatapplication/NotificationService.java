package com.ntnu.mj.lab4_chatapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by markusja on 4/2/18.
 */

public class NotificationService extends Service implements NotificationModel {
    private NetworkClient client;
    private static boolean isRunning = false;
    private static final String NOTIFICATION_TITLE = "New message received";

    @Override
    public void onCreate(){
        client = new NetworkClient();
        client.registerListener(new CustomObserver(client, this));
        Toast.makeText(this, "Service started", Toast.LENGTH_LONG).show();
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


    @Override
    public void newMessageReceived(Message message) {
        if(message != null) {
            Toast.makeText(this, "New message received", Toast.LENGTH_LONG).show();
            createNotification(message);
        }
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
}
