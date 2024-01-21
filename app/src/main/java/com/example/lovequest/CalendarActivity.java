package com.example.lovequest;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lovequest.utils.FirebaseUtil;
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
    private ImageButton backButton;
    private String selectedDate;
    private String chatroomId;

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
        backButton = findViewById(R.id.backButton);

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
        backButton.setOnClickListener(v -> finish());
    }

    private void saveEvent() {
        String eventName = addEventEditText.getText().toString().trim();
        if (eventName.isEmpty()) {
            Toast.makeText(this, "Please enter event name", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> eventInfo = new HashMap<>();
        eventInfo.put("eventDate", selectedDate);
        eventInfo.put("eventDescription", eventName);

        // Extract user IDs from the chatroomId and update each user
        String[] userIds = chatroomId.split("_");
        for (String userId : userIds) {
            db.collection("Users")
                    .document(userId)
                    .set(eventInfo, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Toast.makeText(CalendarActivity.this, "Event updated for user: " + userId, Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(CalendarActivity.this, "Error updating event for user: " + userId, Toast.LENGTH_SHORT).show());
        }
    }
}