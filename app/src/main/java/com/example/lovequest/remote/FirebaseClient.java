package com.example.lovequest.remote;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.lovequest.model.DataModel;
import com.example.lovequest.utils.ErrorCallBack;
import com.example.lovequest.utils.NewEventCallBack;
import com.example.lovequest.utils.SuccessCallBack;
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

    public void login(String email, SuccessCallBack callBack){
        dbRef.child(email.replace(".", ",")).setValue("").addOnCompleteListener(task -> {
            currentEmail = email;
            callBack.onSuccess();
        });
    }

    public void sendMessageToOtherUser(DataModel dataModel, ErrorCallBack errorCallBack){
        String targetEmailPath = dataModel.getTarget().replace(".", ",");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(targetEmailPath).exists()){
                    dbRef.child(targetEmailPath).child(LATEST_EVENT_FIELD_NAME)
                            .setValue(gson.toJson(dataModel));
                } else {
                    errorCallBack.onError();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorCallBack.onError();
            }
        });
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
                            String data = Objects.requireNonNull(snapshot.getValue()).toString();
                            DataModel dataModel = gson.fromJson(data, DataModel.class);
                            callBack.onNewEventReceived(dataModel);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }
}