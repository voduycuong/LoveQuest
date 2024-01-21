package com.example.lovequest.remote;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.lovequest.model.DataModel;
import com.example.lovequest.utils.ErrorCallBack;
import com.example.lovequest.utils.FirebaseUtil;
import com.example.lovequest.utils.NewEventCallBack;
import com.example.lovequest.utils.SuccessCallBack;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.Objects;

public class FirebaseClient {

    private final Gson gson = new Gson();
    private final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    private String currentEmail;
    private static final String LATEST_EVENT_FIELD_NAME = "latest_event";

    public void login(SuccessCallBack callBack){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            currentEmail = user.getEmail();
            // Your login logic
            callBack.onSuccess();
        } else {
            // Handle login failure
        }
    }

    public void setCurrentEmail(String email) {
        this.currentEmail = email;
    }

    public void sendMessageToOtherUser(DataModel dataModel, ErrorCallBack errorCallBack) {
        FirebaseUtil.getChatPartnerEmail(dataModel.getTarget())
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String email = documentSnapshot.getString("email");
                        if (email != null) {
                            String targetEmailPath = email.replace(".", ",");
                            dbRef.child(targetEmailPath).child(LATEST_EVENT_FIELD_NAME)
                                    .setValue(gson.toJson(dataModel));
                        } else {
                            errorCallBack.onError();
                        }
                    } else {
                        errorCallBack.onError();
                    }
                })
                .addOnFailureListener(e -> errorCallBack.onError());
    }

    public void observeIncomingLatestEvent(NewEventCallBack callBack){
        if(currentEmail == null) {
            Log.e("FirebaseClient", "Current email is null, cannot observe incoming events.");
            return;
        }
        String emailPath = currentEmail.replace(".", ",");
        dbRef.child(emailPath).child(LATEST_EVENT_FIELD_NAME).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            if (snapshot.exists() && snapshot.getValue() != null) {
                                // Handle the snapshot data
                                String data = snapshot.getValue(String.class);
                                // Continue with your logic
                            } else {
                                Log.d("FirebaseClient", "Snapshot is empty or null.");
                            }
                        } catch (Exception e) {
                            Log.e("FirebaseClient", "Error in onDataChange: " + e.getMessage());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }
}