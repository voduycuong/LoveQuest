package com.example.lovequest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class BeginActivity3 extends AppCompatActivity {

    private TextView textViewSelectedCountry;
    private TextView textViewSelectedReligion;
    private EditText editTextNationality;
    private EditText editTextCity;
    private Button continue3BTN;
    private Button buttonSelectCountry;
    private Button buttonSelectReligion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin3);

        textViewSelectedCountry = findViewById(R.id.textViewSelectedCountry);
        textViewSelectedReligion = findViewById(R.id.textViewSelectedReligion);
        editTextNationality = findViewById(R.id.nationality);
        editTextCity = findViewById(R.id.city);
        continue3BTN = findViewById(R.id.continuebtn3);
        buttonSelectCountry = findViewById(R.id.buttonSelectCountry);
        buttonSelectReligion = findViewById(R.id.buttonSelectReligion);

        buttonSelectCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCountryListDialog();
            }
        });

        buttonSelectReligion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReligionListDialog();
            }
        });

        continue3BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateInputs()) {
                    saveUserLocationAndReligion();
                }
            }
        });
    }

    private void showCountryListDialog() {
        final String[] countries = getResources().getStringArray(R.array.country_array);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a country");
        builder.setItems(countries, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String country = countries[which];
                textViewSelectedCountry.setText(country);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showReligionListDialog() {
        final String[] religions = getResources().getStringArray(R.array.religion_array);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a religion");
        builder.setItems(religions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String religion = religions[which];
                textViewSelectedReligion.setText(religion);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean validateInputs() {
        String country = textViewSelectedCountry.getText().toString();
        String religion = textViewSelectedReligion.getText().toString();
        String nationality = editTextNationality.getText().toString();
        String city = editTextCity.getText().toString();

        if (country.isEmpty() || religion.isEmpty() || nationality.isEmpty() || city.isEmpty()) {
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveUserLocationAndReligion() {
        String country = textViewSelectedCountry.getText().toString();
        String religion = textViewSelectedReligion.getText().toString();
        String nationality = editTextNationality.getText().toString();
        String city = editTextCity.getText().toString();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore.getInstance().collection("Users")
                    .document(currentUser.getUid())
                    .update("country", country, "religion", religion, "nationality", nationality, "city", city)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(BeginActivity3.this, "Location and Religion Saved", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(BeginActivity3.this, BeginActivity4.class));
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(BeginActivity3.this, "Error saving information", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show();
        }
    }
}
