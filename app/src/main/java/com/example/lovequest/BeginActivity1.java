package com.example.lovequest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.app.DatePickerDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lovequest.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class BeginActivity1 extends AppCompatActivity {

    private TextView tvSelectedDate;
    private String userEmail, userId;
    private Button btnDatePicker, continue1BTN;
    private EditText inputAge, inputName, inputJob, inputUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin1);

        userEmail = getIntent().getStringExtra("email");
        userId = getIntent().getStringExtra("userId");

        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        btnDatePicker = findViewById(R.id.btnDatePicker);
        inputAge = findViewById(R.id.input_age);
        inputName = findViewById(R.id.input_name);
        inputUsername = findViewById(R.id.input_username);
        inputJob = findViewById(R.id.input_job);
        continue1BTN = findViewById(R.id.continuebtn1);

        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(BeginActivity1.this,
                        (view, year1, monthOfYear, dayOfMonth) -> {
                            String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                            tvSelectedDate.setText(selectedDate);

                            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                            int age = currentYear - year1;
                            inputAge.setText(String.valueOf(age));
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        continue1BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateInputs()) {
                    saveUserProfile();
                }
            }
        });
    }

    private boolean validateInputs() {
        String name = inputName.getText().toString().trim();
        String username = inputUsername.getText().toString();
        String dateOfBirth = tvSelectedDate.getText().toString().trim();
        String age = inputAge.getText().toString().trim();
        String job = inputJob.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (username.isEmpty()) {
            Toast.makeText(this, "Please select your username", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (dateOfBirth.isEmpty()) {
            Toast.makeText(this, "Please select your date of birth", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (age.isEmpty()) {
            Toast.makeText(this, "Age cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (job.isEmpty()) {
            Toast.makeText(this, "Please enter your job", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveUserProfile() {
        String name = inputName.getText().toString();
        String username = inputUsername.getText().toString();
        String dateOfBirth = tvSelectedDate.getText().toString();
        String age = inputAge.getText().toString();
        String job = inputJob.getText().toString();

        UserModel userModel = new UserModel();
        userModel.setEmail(userEmail);
        userModel.setUserId(userId);
        userModel.setName(name);
        userModel.setUsername(username);
        userModel.setDateOfBirth(dateOfBirth);
        userModel.setAge(age);
        //userModel.setJob(job);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore.getInstance().collection("Users").document(currentUser.getUid()).set(userModel)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(BeginActivity1.this, "Profile Saved", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(BeginActivity1.this, BeginActivity2.class);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(BeginActivity1.this, "Error saving profile", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show();
        }
    }
}
