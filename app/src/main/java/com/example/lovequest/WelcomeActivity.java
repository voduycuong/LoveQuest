package com.example.lovequest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //Button to Begin
        Button beginBTN = findViewById(R.id.beginBtn);
        //Button to Sign in
        Button signinBTN = findViewById(R.id.signinBtn);

        //OnClickListener for beginButtons
        beginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start sign in activity
                Intent intent = new Intent(WelcomeActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //OnClickListener for signinButtons
        signinBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start sign in activity
                Intent intent = new Intent(WelcomeActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}