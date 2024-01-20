package com.example.lovequest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class BeginActivity4 extends AppCompatActivity {

    private Button continue4BTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin4);

        continue4BTN = findViewById(R.id.continuebtn4);

        continue4BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the HomeScreen activity
                Intent intent = new Intent(BeginActivity4.this, HomeScreen.class);
                startActivity(intent);
                finish();
            }
        });
    }
}