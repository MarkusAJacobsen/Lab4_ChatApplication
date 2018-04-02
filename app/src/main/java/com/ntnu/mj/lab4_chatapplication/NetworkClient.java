package com.ntnu.mj.lab4_chatapplication;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
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

import static android.content.ContentValues.TAG;

/**
 * Created by markusja on 3/9/18.
 */

public class NetworkClient {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String MESSAGES_COLLECTION = "messages";
    private static final String FIELD_USERNAME = "username";
    private static final String FIELD_TEXT = "description";
    private static final String FIELD_TIMESTAMP = "timestamp";

    ArrayList<Message> resultCache;
    ArrayList<User> userCache;
    ArrayList<UpdateListener> listeners = new ArrayList<>();

    public NetworkClient() {
        resultCache = new ArrayList<>();
        userCache = new ArrayList<>();
        fetchUsers();
        fetchMessages();
    }

    public String publishMessage(Message message) {
        final String[] messageId = new String[1];

        db.collection(MESSAGES_COLLECTION).add(message)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        messageId[0] =  documentReference.getId();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);

                    }
                });

        return messageId[0];
    }

    public void fetchMessages(){
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
                        for(UpdateListener listerner : listeners) {
                            listerner.UpdateMessages(localCache);
                        }
                    }
                });
    }

    public void fetchUsers() {

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            List<User> users = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                User user = new User(document.getString("name"), document.getString("userId"));
                                users.add(user);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }

                            setUsers((ArrayList<User>) users);
                            for(UpdateListener listener : listeners) {
                                listener.UpdateUsers((ArrayList<User>) users);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private Message getLatestMessage(){
        if(!this.resultCache.isEmpty()) {
            return this.resultCache.get(0);
        }
        return null;
    }

    public void triggerListeners(){
        for(UpdateListener listener : listeners) {
            listener.UpdateMessages((ArrayList<Message>) getMessages());
            listener.UpdateUsers((ArrayList<User>) getUsers());
            listener.NotifyMessageNotificationManager(getLatestMessage());
        }
    }

    public void registerListener(CustomObserver object) {
        listeners.add(object);
    }

    public List<Message> getMessages(){
        return this.resultCache;
    }

    public void setMessages(ArrayList<Message> newMessages) {
        this.resultCache = newMessages;
    }

    public List<User> getUsers() {
        return this.userCache;
    }

    public void setUsers(ArrayList<User> newUsers) {
        this.userCache = newUsers;
    }


}