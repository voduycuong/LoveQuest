package com.example.lovequest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class EventDetailsActivity extends AppCompatActivity {

    private TextView tvEventName, tvEventDate, tvEventDescription, tvEventLocation;
    private ImageView backBtn;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        eventId = getIntent().getStringExtra("EVENT_ID");

        initializeViews();
        loadEventDetails();
    }

    private void initializeViews() {
        tvEventName = findViewById(R.id.tvEventName);
        tvEventDate = findViewById(R.id.tvEventDate);
        tvEventDescription = findViewById(R.id.tvEventDescription);
        tvEventLocation = findViewById(R.id.tvEventLocation);
        backBtn = findViewById(R.id.back_button);

        backBtn.setOnClickListener(view -> onBackPressed());
    }

    private void loadEventDetails() {
        db.collection("dates").document(eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            updateEventInformation(document);
                        } else {
                            Toast.makeText(this, "Event not found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Error loading event details.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateEventInformation(DocumentSnapshot event) {
        tvEventName.setText(event.getString("name"));
        tvEventDate.setText(event.getString("date"));
        tvEventDescription.setText(event.getString("description"));
        tvEventLocation.setText(event.getString("location"));

        // If needed, add logic to handle event location clicks (e.g., open Google Maps)
    }

    // Additional methods if required for handling specific actions like map directions
}
