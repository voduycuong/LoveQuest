package com.example.lovequest.ui.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.lovequest.model.UserModel;
import com.example.lovequest.utils.FirebaseUtil;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends ViewModel {

    private final MutableLiveData<List<UserModel>> filteredUsersLiveData = new MutableLiveData<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ChatViewModel() {
        loadFilteredUsers();
    }

    public LiveData<List<UserModel>> getFilteredUsersLiveData() {
        return filteredUsersLiveData;
    }

    private void loadFilteredUsers() {
        FirebaseUtil.getUserReference().get().addOnSuccessListener(documentSnapshot -> {
            UserModel currentUser = documentSnapshot.toObject(UserModel.class);
            if (currentUser != null && currentUser.getGender() != null) {
                String oppositeGender = currentUser.getGender().equalsIgnoreCase("Male") ? "Female" : "Male";
                fetchUsersWithOppositeGender(oppositeGender);
            }
        });
    }

    private void fetchUsersWithOppositeGender(String gender) {
        db.collection("Users")
                .whereEqualTo("gender", gender)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            List<UserModel> filteredUsers = new ArrayList<>();
                            for (DocumentSnapshot snapshot : querySnapshot.getDocuments()) {
                                UserModel user = snapshot.toObject(UserModel.class);
                                if (user != null) {
                                    filteredUsers.add(user);
                                }
                            }
                            filteredUsersLiveData.setValue(filteredUsers);
                        }
                    } else {
                        // Handle the error...
                    }
                });
    }
}