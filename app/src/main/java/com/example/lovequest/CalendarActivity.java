package com.example.lovequest;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        databaseReference = FirebaseDatabase.getInstance().getReference("Calendar");
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

    public void addEvent(View v) {
        databaseReference.child(selectedDate).setValue(addEventEditText.getText().toString());
    }
}