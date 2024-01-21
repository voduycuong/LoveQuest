package com.example.lovequest;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class CalendarActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private Button addButton;
    private EditText addEventEditText;
    private String selectedDate;
    private String chatroomId;
    private FirebaseFirestore db;
    private String[] userIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        chatroomId = getIntent().getStringExtra("chatroomId");
        if (chatroomId == null) {
            Toast.makeText(this, "Chatroom ID is missing", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        calendarView = findViewById(R.id.calendarView);
        addEventEditText = findViewById(R.id.addEventEditText);
        addButton = findViewById(R.id.addButton);
        db = FirebaseFirestore.getInstance();
        userIds = chatroomId.split("_");

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                selectedDate = sdf.format(calendar.getTime());
            }
        });

        addButton.setOnClickListener(v -> saveEvent());
    }

    private void saveEvent() {
        String eventName = addEventEditText.getText().toString().trim();
        if (eventName.isEmpty()) {
            Toast.makeText(this, "Please enter event name", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> eventInfo = new HashMap<>();
        eventInfo.put("eventDate", selectedDate);
        eventInfo.put("eventDescription", eventName);

        // Update for the first user
        updateUserEvent(userIds[0], eventInfo);
    }

    private void updateUserEvent(String userId, Map<String, Object> eventInfo) {
        Log.d("CalendarActivity", "Attempting to update event for user: " + userId);
        db.collection("Users")
                .document(userId)
                .set(eventInfo, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d("CalendarActivity", "Event updated successfully for user: " + userId);
                    Toast.makeText(CalendarActivity.this, "Event updated for user: " + userId, Toast.LENGTH_SHORT).show();
                    if (!userId.equals(userIds[1])) {
                        updateUserEvent(userIds[1], eventInfo);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("CalendarActivity", "Error updating event for user: " + userId, e);
                    Toast.makeText(CalendarActivity.this, "Error updating event for user: " + userId, Toast.LENGTH_SHORT).show();
                });
    }

}