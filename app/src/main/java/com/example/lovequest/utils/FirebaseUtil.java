package com.example.lovequest.utils;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {

    public static String getCurrentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    public static boolean isAuthenticated() {
        return getCurrentUserId() != null;
    }

    public static DocumentReference getUserReference() {
        return FirebaseFirestore.getInstance().collection("Users").document(getCurrentUserId());
    }

    public static CollectionReference getAllUserReference() {
        return FirebaseFirestore.getInstance().collection("Users");
    }

    public static DocumentReference getChatroomRef(String chatroomId) {
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    public static CollectionReference getChatroomMessagesRef(String chatroomId) {
        return getChatroomRef(chatroomId).collection("chats");
    }

    public static String createChatroomId(String firstUserId, String secondUserId) {
        return firstUserId.hashCode() < secondUserId.hashCode() ? firstUserId + "_" + secondUserId : secondUserId + "_" + firstUserId;
    }

    public static CollectionReference getChatroomsReference() {
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    public static DocumentReference getChatPartnerRef(List<String> userIds) {
        String otherUserId = userIds.get(0).equals(getCurrentUserId()) ? userIds.get(1) : userIds.get(0);
        return getAllUserReference().document(otherUserId);
    }

    public static String formatTimestamp(Timestamp timestamp) {
        return new SimpleDateFormat("HH:mm").format(timestamp.toDate());
    }

    public static void performLogout() {
        FirebaseAuth.getInstance().signOut();
    }

    public static StorageReference getCurrentUserPhotoRef() {
        return FirebaseStorage.getInstance().getReference().child("profile_pic").child(getCurrentUserId());
    }

    public static StorageReference getOtherUserPhotoRef(String userId) {
        return FirebaseStorage.getInstance().getReference().child("profile_pic").child(userId);
    }

    public static Task<DocumentSnapshot> getChatPartnerEmail(String chatroomId) {
        return getChatPartnerRef(Arrays.asList(FirebaseAuth.getInstance().getCurrentUser().getUid(), chatroomId))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // You can retrieve email like this, assuming you have email field in your user document
                        String email = documentSnapshot.getString("email");
                    }
                });
    }
}