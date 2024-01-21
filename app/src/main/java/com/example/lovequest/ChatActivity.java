package com.example.lovequest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lovequest.adapter.ChatRecyclerAdapter;
import com.example.lovequest.model.ChatMessageModel;
import com.example.lovequest.model.ChatroomModel;
import com.example.lovequest.model.UserModel;
import com.example.lovequest.utils.AndroidUtil;
import com.example.lovequest.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;

import java.io.IOException;
import java.util.Arrays;
import org.json.JSONObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {

    private UserModel otherUser;
    private String chatroomId;
    private ChatroomModel chatroomModel;
    private ChatRecyclerAdapter chatAdapter;

    private EditText messageInput;
    private ImageButton btnSendMessage, btnBack, btnCall;
    private TextView txtOtherUsername;
    private RecyclerView chatRecyclerView;
    private ImageView imgProfile;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initializeViews();
        prepareChatroom();
        setupRecyclerView();
        handleMessages();
        requestCameraPermission();
    }

    private void initializeViews() {
        otherUser = AndroidUtil.extractUserFromIntent(getIntent());
        chatroomId = FirebaseUtil.createChatroomId(FirebaseUtil.getCurrentUserId(), otherUser.getUserId());

        btnCall = findViewById(R.id.call_btn);
        messageInput = findViewById(R.id.chat_message_input);
        btnSendMessage = findViewById(R.id.message_send_btn);
        btnBack = findViewById(R.id.back_btn);
        txtOtherUsername = findViewById(R.id.other_username);
        chatRecyclerView = findViewById(R.id.chat_recycler_view);
        imgProfile = findViewById(R.id.profile_pic_image_view);

        FirebaseUtil.getOtherUserPhotoRef(otherUser.getUserId()).getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        AndroidUtil.loadProfileImage(this, task.getResult(), imgProfile);
                    }
                });

        btnBack.setOnClickListener(v -> onBackPressed());
        txtOtherUsername.setText(otherUser.getUsername());
        btnCall.setOnClickListener(v -> startCall());
    }

    private void handleMessages() {
        btnSendMessage.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if(!message.isEmpty()) {
                sendMessage(message);
            }
        });
    }

    private void setupRecyclerView() {
        Query query = FirebaseUtil.getChatroomMessagesRef(chatroomId)
                .orderBy("timestamp", Query.Direction.ASCENDING); // Changed to ASCENDING
        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class).build();

        chatAdapter = new ChatRecyclerAdapter(options, getApplicationContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // To keep the view scrolled to the bottom
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.setAdapter(chatAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (chatAdapter != null) {
            chatAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (chatAdapter != null) {
            chatAdapter.stopListening();
        }
    }

    private void sendMessage(String message) {
        if (chatroomModel == null) {
            chatroomModel = new ChatroomModel();
            chatroomModel.setChatroomId(chatroomId);
            chatroomModel.setUserIds(Arrays.asList(FirebaseUtil.getCurrentUserId(), otherUser.getUserId()));
        }
        ChatMessageModel newMessage = new ChatMessageModel(message, FirebaseUtil.getCurrentUserId(), Timestamp.now());
        FirebaseUtil.getChatroomMessagesRef(chatroomId).add(newMessage)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        messageInput.setText("");
                        updateChatroom(message);
                    } else {
                        Log.e("ChatActivity", "Failed to add message", task.getException());
                    }
                });
    }

    private void prepareChatroom() {
        FirebaseUtil.getChatroomRef(chatroomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ChatroomModel existingChatroomModel = task.getResult().toObject(ChatroomModel.class);
                if (existingChatroomModel != null) {
                    chatroomModel = existingChatroomModel;
                } else {
                    chatroomModel = new ChatroomModel();
                    chatroomModel.setChatroomId(chatroomId);
                    chatroomModel.setUserIds(Arrays.asList(FirebaseUtil.getCurrentUserId(), otherUser.getUserId()));
                    updateChatroom(""); // Initialize with empty last message
                }
            }
        });
    }

    private void updateChatroom(String message) {
        chatroomModel.setLastMessage(message);
        chatroomModel.setLastMessageSenderId(FirebaseUtil.getCurrentUserId());
        chatroomModel.setLastMessageTimestamp(Timestamp.now());

        FirebaseUtil.getChatroomRef(chatroomId).set(chatroomModel)
                .addOnSuccessListener(aVoid -> Log.d("ChatActivity", "Chatroom updated successfully"))
                .addOnFailureListener(e -> Log.e("ChatActivity", "Error updating chatroom", e));
    }

    private void startCall() {
        Intent intent = new Intent(ChatActivity.this, CallActivity.class);
        intent.putExtra("targetUsername", otherUser.getUsername());
        startActivity(intent);
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            // Permission has already been granted, continue with camera operation
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, continue as usual
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void notifyUser(String message) {
        FirebaseUtil.getUserReference().get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                UserModel currentUser = task.getResult().toObject(UserModel.class);
                sendNotification(currentUser, message, otherUser.getFcmToken());
            }
        });
    }

    private void sendNotification(UserModel sender, String message, String recipientToken) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("to", recipientToken);
            jsonObject.put("notification", new JSONObject().put("title", sender.getUsername()).put("body", message));
            jsonObject.put("data", new JSONObject().put("userId", sender.getUserId()));
            makeApiCall(jsonObject);
        } catch (Exception e) {
            Log.e("ChatActivity", "Error in sending notification: " + e.getMessage());
        }
    }

    private void makeApiCall(JSONObject jsonObject) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .post(body)
                .header("Authorization", "Bearer YOUR_API_KEY")
                .build();
        client.newCall(request).enqueue(new NotificationCallback());
    }

    private static class NotificationCallback implements Callback {
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            Log.e("ChatActivity", "API call failed: " + e.getMessage());
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            if (!response.isSuccessful()) {
                Log.e("ChatActivity", "Unexpected response: " + response);
            }
        }
    }
}