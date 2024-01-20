package com.example.lovequest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class BeginActivity2 extends AppCompatActivity {

    String[] orientations = {"Heterosexual", "Homosexual", "Bisexual", "Asexual", "Pansexual", "Queer", "Questioning"};
    private Button continue2BTN;
    private CustomAdapter1 adapter; // Declare the custom adapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin2);

        continue2BTN = findViewById(R.id.continuebtn2);

        // Initialize the ListView and set the custom adapter
        ListView listView = findViewById(R.id.listView);
        CustomAdapter1 adapter = new CustomAdapter1(this, orientations);
        listView.setAdapter(adapter);

        RadioGroup radioGroup = findViewById(R.id.radioGroupGender);
        RadioButton radioMale = findViewById(R.id.radioButtonMale);
        RadioButton radioFemale = findViewById(R.id.radioButtonFemale);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Handle radio button selection
                if (checkedId == R.id.radioButtonMale) {
                    // Male selected
                    Toast.makeText(getApplicationContext(), "Selected: Male", Toast.LENGTH_SHORT).show();
                } else if (checkedId == R.id.radioButtonFemale) {
                    // Female selected
                    Toast.makeText(getApplicationContext(), "Selected: Female", Toast.LENGTH_SHORT).show();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSelectedItem(position); // Update the selection state in the adapter
                String selectedItem = orientations[position];
                // Handle the selected item
                Toast.makeText(getApplicationContext(), "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
            }
        });

        continue2BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start the next activity
                Intent intent = new Intent(BeginActivity2.this, BeginActivity3.class);
                startActivity(intent);
                finish();
            }
        });
    }
}