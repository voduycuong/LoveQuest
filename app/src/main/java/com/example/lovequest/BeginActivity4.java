package com.example.lovequest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lovequest.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class BeginActivity4 extends AppCompatActivity {

    private EditText hobbiesEditText, descriptionEditText;
    private Button continue4BTN;
    private ImageView profileImageView;
    private Uri selectedImageUri;

    // Firebase instances
    private StorageReference storageReference;
    private FirebaseFirestore firestore;

    // ActivityResultLauncher for the image picker
    private final ActivityResultLauncher<Intent> imagePickLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            selectedImageUri = result.getData().getData();
                            profileImageView.setImageURI(selectedImageUri);
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin4);

        hobbiesEditText = findViewById(R.id.hobbies);
        descriptionEditText = findViewById(R.id.description);
        continue4BTN = findViewById(R.id.continuebtn4);
        profileImageView = findViewById(R.id.profileImageView);

        // Initialize Firebase instances
        storageReference = FirebaseStorage.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();

        profileImageView.setOnClickListener(v -> {
            // Intent to pick an image
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickLauncher.launch(intent);
        });

        continue4BTN.setOnClickListener(view -> {
            if (validateInputs()) {
                if (selectedImageUri != null) {
                    uploadImageAndSaveProfile();
                } else {
                    Toast.makeText(this, "Please select a profile image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateInputs() {
        String hobbies = hobbiesEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        // Add other validation checks if needed
        return !hobbies.isEmpty() && !description.isEmpty();
    }

    private void uploadImageAndSaveProfile() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference fileRef = storageReference.child("users/" + userId + "/profile.jpg");

        fileRef.putFile(selectedImageUri).addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String imageUrl = uri.toString();
            saveUserProfile(imageUrl);
        })).addOnFailureListener(e -> Toast.makeText(BeginActivity4.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveUserProfile(String imageUrl) {
        String hobbies = hobbiesEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        UserModel userModel = new UserModel();
        // Set other fields if needed
        userModel.setHobbies(hobbies);
        userModel.setDescription(description);
        userModel.setPhotoUrl(imageUrl);

        firestore.collection("Users").document(userId)
                .update("hobbies", hobbies, "description", description, "photoUrl", imageUrl)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(BeginActivity4.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(BeginActivity4.this, HomeScreen.class));
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(BeginActivity4.this, "Error updating profile", Toast.LENGTH_SHORT).show());
    }
}