package com.example.lovequest.ui.user;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.lovequest.model.UserModel;
import com.example.lovequest.utils.FirebaseUtil;
import com.google.firebase.firestore.DocumentReference;

public class UserViewModel extends ViewModel {

    private MutableLiveData<UserModel> userLiveData;

    public UserViewModel() {
        userLiveData = new MutableLiveData<>();
        loadUserData();
    }

    public LiveData<UserModel> getUserLiveData() {
        return userLiveData;
    }

    private void loadUserData() {
        DocumentReference userRef = FirebaseUtil.getUserReference();
        if (userRef != null) {
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    UserModel userModel = task.getResult().toObject(UserModel.class);
                    userLiveData.postValue(userModel);
                    Log.d("UserViewModel", "User data loaded successfully");
                } else {
                    Log.e("UserViewModel", "Error loading user data", task.getException());
                }
            });
        } else {
            Log.e("UserViewModel", "UserRef is null");
        }
    }

}