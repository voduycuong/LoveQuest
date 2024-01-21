package com.example.lovequest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.lovequest.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfilePageActivity extends AppCompatActivity {

    private TextView textViewName, textViewEmail, textViewUsername, textViewDob, textViewGender, textViewOrientation, textViewCountry, textViewNationality, textViewHobbies, textViewDescription;
    private ImageView imageViewProfile;
    private FirebaseUser currentUser;
    private DocumentReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        // Initialize views
        textViewName = findViewById(R.id.text_view_name);
        textViewEmail = findViewById(R.id.text_view_email);
        textViewUsername = findViewById(R.id.editText_view_username);
        textViewDob = findViewById(R.id.text_view_dob);
        textViewGender = findViewById(R.id.text_view_gender);
        textViewOrientation = findViewById(R.id.text_view_orientation);
        textViewCountry = findViewById(R.id.text_view_country);
        textViewNationality = findViewById(R.id.text_view_nationality);
        textViewHobbies = findViewById(R.id.text_view_hobbies);
        textViewDescription= findViewById(R.id.text_view_description);
        imageViewProfile = findViewById(R.id.image_view_profile);

        // Get current user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            databaseReference = FirebaseFirestore.getInstance().collection("users").document(currentUser.getUid());
            getUserInfo();
        }
    }

    private void getUserInfo() {
        databaseReference.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                UserModel userModel = documentSnapshot.toObject(UserModel.class);

                // Update UI with user information
                textViewName.setText(userModel.getName());
                textViewEmail.setText(userModel.getEmail());
                textViewUsername.setText(userModel.getUsername());
                textViewDob.setText(userModel.getDateOfBirth());
                textViewGender.setText(userModel.getGender());
                textViewOrientation.setText(userModel.getOrientation());
                textViewCountry.setText(userModel.getCountry());
                textViewNationality.setText(userModel.getNationality());
                textViewHobbies.setText(userModel.getHobbies());
                textViewDescription.setText(userModel.getDescription());

                if (userModel.getPhotoUrl() != null && !userModel.getPhotoUrl().isEmpty()) {
                    Glide.with(this).load(userModel.getPhotoUrl()).into(imageViewProfile);
                }
            }
        }).addOnFailureListener(e -> {
            // Handle error
        });
    }

}
