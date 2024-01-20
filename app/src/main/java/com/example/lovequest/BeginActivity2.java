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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class BeginActivity2 extends AppCompatActivity {

    String[] orientations = {"Heterosexual", "Homosexual", "Bisexual", "Asexual", "Pansexual", "Queer", "Questioning"};
    private Button continue2BTN;
    private CustomAdapter1 adapter; // Assuming CustomAdapter1 is defined elsewhere in your code
    private RadioGroup radioGroupGender;
    private String selectedGender = "";
    private String selectedOrientation = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin2);

        continue2BTN = findViewById(R.id.continuebtn2);
        radioGroupGender = findViewById(R.id.radioGroupGender);

        // Initialize the ListView and set the custom adapter
        ListView listView = findViewById(R.id.listView);
        adapter = new CustomAdapter1(this, orientations);
        listView.setAdapter(adapter);

        radioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton selectedRadioButton = findViewById(checkedId);
                selectedGender = selectedRadioButton.getText().toString();
                Toast.makeText(getApplicationContext(), "Selected: " + selectedGender, Toast.LENGTH_SHORT).show();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSelectedItem(position); // Update the selection state in the adapter
                selectedOrientation = orientations[position];
                Toast.makeText(getApplicationContext(), "Selected: " + selectedOrientation, Toast.LENGTH_SHORT).show();
            }
        });

        continue2BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateSelections()) {
                    saveUserPreferences();
                }
            }
        });
    }

    private boolean validateSelections() {
        if (selectedGender.isEmpty()) {
            Toast.makeText(this, "Please select your gender", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedOrientation.isEmpty()) {
            Toast.makeText(this, "Please select your orientation", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveUserPreferences() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore.getInstance().collection("Users")
                    .document(currentUser.getUid())
                    .update("gender", selectedGender, "orientation", selectedOrientation)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(BeginActivity2.this, "Preferences Saved", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(BeginActivity2.this, BeginActivity3.class));
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(BeginActivity2.this, "Error saving preferences", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Please select your gender and orientation", Toast.LENGTH_SHORT).show();
        }
    }
}
