package com.example.lovequest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class BeginActivity4 extends AppCompatActivity {

    private EditText hobbiesEditText;
    private EditText descriptionEditText;
    private Button continue4BTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin4);

        hobbiesEditText = findViewById(R.id.hobbies);
        descriptionEditText = findViewById(R.id.description);
        continue4BTN = findViewById(R.id.continuebtn4);

        continue4BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateInputs()) {
                    saveUserProfile();
                }
            }
        });
    }

    private boolean validateInputs() {
        String hobbies = hobbiesEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (hobbies.isEmpty()) {
            Toast.makeText(this, "Please enter your hobbies", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (description.isEmpty()) {
            Toast.makeText(this, "Please enter your profile description", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveUserProfile() {
        String hobbies = hobbiesEditText.getText().toString();
        String description = descriptionEditText.getText().toString();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore.getInstance().collection("Users")
                    .document(currentUser.getUid())
                    .update("hobbies", hobbies, "description", description)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(BeginActivity4.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(BeginActivity4.this, HomeScreen.class));
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(BeginActivity4.this, "Error updating profile", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show();
        }
    }
}
