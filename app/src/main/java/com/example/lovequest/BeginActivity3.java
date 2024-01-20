package com.example.lovequest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BeginActivity3 extends AppCompatActivity {

    private TextView textViewSelectedCountry;
    private TextView textViewSelectedReligion;


    private Button continue3BTN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin3);

        Button buttonSelectCountry = findViewById(R.id.buttonSelectCountry);
        textViewSelectedCountry = findViewById(R.id.textViewSelectedCountry);


        Button buttonSelectReligion = findViewById(R.id.buttonSelectReligion);
        textViewSelectedReligion = findViewById(R.id.textViewSelectedReligion);

        buttonSelectCountry.setOnClickListener(view -> showCountryListDialog());
        buttonSelectReligion.setOnClickListener(view -> showReligionListDialog());


        continue3BTN = findViewById(R.id.continuebtn3);


        continue3BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start the next activity
                Intent intent = new Intent(BeginActivity3.this, BeginActivity4.class);
                startActivity(intent);
                finish();
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
                // Update the TextView with the selected country
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
                // Update the TextView with the selected country
                textViewSelectedReligion.setText(religion);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}