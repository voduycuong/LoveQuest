package com.example.lovequest.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.lovequest.R;
import com.example.lovequest.WelcomeActivity;
import com.example.lovequest.model.UserModel;
import com.example.lovequest.utils.AndroidUtil;
import com.example.lovequest.utils.FirebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.StorageReference;

public class UserFragment extends Fragment {

    private ImageView profilePic;
    private EditText usernameInput, nameInput, dateOfBirthInput, ageInput, genderInput, countryInput, nationalityInput, cityInput, descriptionInput, hobbiesInput;
    private Button updateProfileBtn, logoutBtn;
    private UserModel currentUserModel;
    private ActivityResultLauncher<Intent> imagePickLauncher;
    private Uri selectedImageUri;

    private UserViewModel userViewModel;

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            profilePic.setImageURI(selectedImageUri);
                        }
                    }
                });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        profilePic = view.findViewById(R.id.profile_image_view);
        usernameInput = view.findViewById(R.id.profile_username);
        updateProfileBtn = view.findViewById(R.id.profile_update_btn);
        logoutBtn = view.findViewById(R.id.logout_btn);
        nameInput = view.findViewById(R.id.profile_name);
        dateOfBirthInput = view.findViewById(R.id.profile_dateOfBirth);
        ageInput = view.findViewById(R.id.profile_age);
        genderInput = view.findViewById(R.id.profile_gender);
        countryInput = view.findViewById(R.id.profile_country);
        nationalityInput = view.findViewById(R.id.profile_nationality);
        cityInput = view.findViewById(R.id.profile_city);
        descriptionInput = view.findViewById(R.id.profile_description);
        hobbiesInput = view.findViewById(R.id.profile_hobbies);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getUserLiveData().observe(getViewLifecycleOwner(), this::populateUserData);

//        updateProfileBtn.setOnClickListener(v -> updateBtnClick());

        logoutBtn.setOnClickListener(v -> {
            // Handle the logout logic here
            // For example, sign out the user if necessary
            FirebaseUtil.performLogout(); // If using Firebase Authentication

            // Now create an Intent to start the WelcomeActivity
            Intent intent = new Intent(getContext(), WelcomeActivity.class);

            // Clear the activity stack and start the new activity
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            // Start the WelcomeActivity
            startActivity(intent);

            // End the current activity
            getActivity().finish();
        });


        profilePic.setOnClickListener(v -> ImagePicker.with(this)
                .cropSquare()
                .compress(512)
                .maxResultSize(512, 512)
                .createIntent(intent -> {
                    imagePickLauncher.launch(intent);
                    return null; // In Java, returning null where Kotlin expects Unit
                }));

        return view;
    }

    private void populateUserData(UserModel userModel) {
        if (userModel != null) {
            usernameInput.setText(userModel.getUsername());
            nameInput.setText(userModel.getName());
            dateOfBirthInput.setText(userModel.getDateOfBirth());
            ageInput.setText(userModel.getAge());
            genderInput.setText(userModel.getGender());
            countryInput.setText(userModel.getCountry());
            nationalityInput.setText(userModel.getNationality());
            cityInput.setText(userModel.getCity());
            descriptionInput.setText(userModel.getDescription());
            hobbiesInput.setText(userModel.getHobbies());
            // Load profile picture if it exists
            if (userModel.getPhotoUrl() != null && !userModel.getPhotoUrl().isEmpty()) {
                Uri photoUri = Uri.parse(userModel.getPhotoUrl());
                AndroidUtil.loadProfileImage(getContext(), photoUri, profilePic);
            }
        }
    }

//    private void updateBtnClick() {
//        if (selectedImageUri != null) {
//            uploadProfileImage(selectedImageUri, this::updateUserProfile);
//        } else {
//            updateUserProfile(null);
//        }
//    }

    private void uploadProfileImage(Uri imageUri, OnImageUploadCompleteListener listener) {
        StorageReference photoRef = FirebaseUtil.getCurrentUserPhotoRef();
        photoRef.putFile(imageUri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                photoRef.getDownloadUrl().addOnSuccessListener(downloadUri -> listener.onImageUploadComplete(downloadUri.toString()));
            } else {
                Toast.makeText(getContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private void updateUserProfile(@Nullable String imageUrl) {
//        // Fetch data from EditTexts and update currentUserModel
//        currentUserModel = new UserModel(); // Create a new UserModel or fetch the existing one
//        currentUserModel.setUsername(usernameInput.getText().toString());
//        // Set other fields of currentUserModel similarly
//
//        if (imageUrl != null) {
//            currentUserModel.setPhotoUrl(imageUrl);
//        }
//
//        DocumentReference userRef = FirebaseUtil.getUserReference();
//        userRef.set(currentUserModel).addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(getContext(), "Profile update failed", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    interface OnImageUploadCompleteListener {
        void onImageUploadComplete(String imageUrl);
    }
}