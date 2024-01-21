package com.example.lovequest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lovequest.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class CalendarActivity extends AppCompatActivity {

    CalendarView calendarView;
    Button addButton;
    EditText addEventEditText;
    String selectedDate;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = findViewById(R.id.calendarView);
        addEventEditText = findViewById(R.id.addEventEditText);
        addButton = findViewById(R.id.addButton);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = Integer.toString(year) + Integer.toString(month + 1) + Integer.toString(dayOfMonth);
                calendarClicked();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEvent();
            }
        });
    }

    private void calendarClicked() {
        databaseReference.child(selectedDate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    addEventEditText.setText(snapshot.getValue().toString());
                } else {
                    addEventEditText.setText("null");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void saveEvent() {
        UserModel userModel = new UserModel();


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore.getInstance().collection("Users").document(currentUser.getUid()).set(userModel)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(CalendarActivity.this, "Event added successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(CalendarActivity.this, "Error saving profile", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean eventValidate() {
        String eventName = addEventEditText.toString().trim();
        if (eventName.isEmpty()) {
            Toast.makeText(this, "Please enter event name", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}