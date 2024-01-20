package com.example.lovequest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lovequest.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SetProfileActivity extends AppCompatActivity {

    private EditText nameEditText, dateOfBirthEditText, genderEditText, countryEditText, nationalityEditText, cityEditText, descriptionEditText;
    private String photoUrl; // You will set this when the user uploads a photo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile);

        nameEditText = findViewById(R.id.nameEditText);
        dateOfBirthEditText = findViewById(R.id.dateOfBirthEditText);
        genderEditText = findViewById(R.id.genderEditText);
        countryEditText = findViewById(R.id.countryEditText);
        nationalityEditText = findViewById(R.id.nationalityEditText);
        cityEditText = findViewById(R.id.cityEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);

        findViewById(R.id.saveProfileButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfile();
            }
        });
    }

    private void saveUserProfile() {
        String name = nameEditText.getText().toString();
        String dateOfBirth = dateOfBirthEditText.getText().toString();
        String gender = genderEditText.getText().toString();
        String country = countryEditText.getText().toString();
        String nationality = nationalityEditText.getText().toString();
        String city = cityEditText.getText().toString();
        String description = descriptionEditText.getText().toString();

        // TODO: Validate the inputs

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            UserModel userModel = new UserModel();
            userModel.setUserId(currentUser.getUid());
            userModel.setName(name);
            userModel.setDateOfBirth(dateOfBirth);
            userModel.setGender(gender);
            userModel.setCountry(country);
            userModel.setNationality(nationality);
            userModel.setCity(city);
            userModel.setDescription(description);
            userModel.setPhotoUrl(photoUrl); // Make sure this is set from photo upload logic

            FirebaseFirestore.getInstance().collection("Users").document(currentUser.getUid()).set(userModel)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(SetProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                        // Redirect to the next activity after profile setup
                        startActivity(new Intent(SetProfileActivity.this, MainActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(SetProfileActivity.this, "Error updating profile", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show();
        }
    }
}