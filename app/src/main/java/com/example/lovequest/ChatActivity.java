package com.example.lovequest;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
    private ImageButton btnSendMessage, btnBack;
    private TextView txtOtherUsername;
    private RecyclerView chatRecyclerView;
    private ImageView imgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initializeViews();
        prepareChatroom();
        setupRecyclerView();
        handleMessages();
    }

    private void initializeViews() {
        otherUser = AndroidUtil.extractUserFromIntent(getIntent());
        chatroomId = FirebaseUtil.createChatroomId(FirebaseUtil.getCurrentUserId(), otherUser.getUserId());

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
        Query query = FirebaseUtil.getChatroomMessagesRef(chatroomId).orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class).build();

        chatAdapter = new ChatRecyclerAdapter(options, getApplicationContext());
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        chatRecyclerView.setAdapter(chatAdapter);
        chatAdapter.startListening();
    }

    private void sendMessage(String message) {
        ChatMessageModel newMessage = new ChatMessageModel(message, FirebaseUtil.getCurrentUserId(), Timestamp.now());
        FirebaseUtil.getChatroomMessagesRef(chatroomId).add(newMessage)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Log.d("ChatActivity", "Message added successfully");
                        messageInput.setText("");
                        updateChatroom(message); // Update the chatroom here
                        notifyUser(message);
                    } else {
                        Log.e("ChatActivity", "Failed to add message", task.getException());
                    }
                });
    }

    private void updateChatroom(String message) {
        Timestamp now = Timestamp.now();
        chatroomModel = new ChatroomModel(chatroomId, Arrays.asList(FirebaseUtil.getCurrentUserId(), otherUser.getUserId()), now, FirebaseUtil.getCurrentUserId(), message);
        FirebaseUtil.getChatroomRef(chatroomId).set(chatroomModel)
                .addOnSuccessListener(aVoid -> Log.d("ChatActivity", "Chatroom created/updated successfully"))
                .addOnFailureListener(e -> Log.e("ChatActivity", "Error creating/updating chatroom", e));
    }

    private void prepareChatroom() {
        FirebaseUtil.getChatroomRef(chatroomId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                chatroomModel = task.getResult().toObject(ChatroomModel.class);
                if(chatroomModel == null) {
                    updateChatroom("");
                }
            }
        });
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