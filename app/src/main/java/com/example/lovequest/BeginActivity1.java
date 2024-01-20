package com.example.lovequest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.app.DatePickerDialog;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Calendar;


public class BeginActivity1 extends AppCompatActivity {

    private TextView tvSelectedDate;
    private Button btnDatePicker;
    private EditText inputAge;

    private Button continue1BTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin1);

        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        btnDatePicker = findViewById(R.id.btnDatePicker);
        inputAge = findViewById(R.id.input_age);
        continue1BTN = findViewById(R.id.continuebtn1);

        // OnClickListener for Choosing Date Button
        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(BeginActivity1.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                tvSelectedDate.setText(selectedDate);

                                // Calculate age and set it in the EditText
                                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                                int age = currentYear - year;
                                inputAge.setText(String.valueOf(age));
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        continue1BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start sign in activity
                Intent intent = new Intent(BeginActivity1.this, BeginActivity2.class);
                startActivity(intent);
                finish();
            }
        });
    }
}