package com.ntnu.mj.lab4_chatapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;

import static android.content.ContentValues.TAG;

/**
 * Created by markusja on 3/11/18.
 */

public class Login extends AppCompatActivity{
    private User user = null;
    private FirebaseUser fbUser = null;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView mUsername;
    private Button mLogin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(getUserFromPreference()){
            startMain();
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        mUsername = findViewById(R.id.username);
        mUsername.setText(randomUsername());

        mLogin = findViewById(R.id.login);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn(String.valueOf(mUsername.getText()));
            }
        });
    }

    private void startMain() {
        Intent intent = new Intent(this, MainActivity.class);
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

    private String randomUsername() {
        StringBuilder builder = new StringBuilder();
        Random randGen = new Random();

        for(int i = 0; i < 6; i++) {
            builder.append(randGen.nextInt(9));
        }
        return "anon" + builder.toString();
    }

    private void signIn(final String username) {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                            fbUser = mAuth.getCurrentUser();
                            user = new User(username, fbUser.getUid());
                            writeToSharedPreferences();

                            db.collection("users")
                                    .add(user)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error adding document", e);

                                        }
                                    });

                            startMain();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });


    }

    private void writeToSharedPreferences(){
        SharedPreferences preferences = getSharedPreferences(SharedPreferencesStatics.PREFS_FILE, MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(SharedPreferencesStatics.FIELD_USERNAME, user.getName());
        editor.putString(SharedPreferencesStatics.FIELD_UID, user.getUserId());

        editor.apply();
    }


}
