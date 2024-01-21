package com.example.lovequest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lovequest.repository.MainRepository;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInActivity extends AppCompatActivity {
    private final int RC_SIGN_IN = 40;
    private ImageView googleSignInBtn;
    private EditText emailEditText, passwordEditText;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private ProgressDialog progressDialog;
    private Button switchToSignUpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        googleSignInBtn = findViewById(R.id.googleSignInBtn);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        switchToSignUpBtn = findViewById(R.id.switchToSignUpBtn);

        firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("815785697369-h01ujjrou2ora4n5dmtj5dugg5ot0udr.apps.googleusercontent.com\n")
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

        switchToSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToSignUp = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(backToSignUp);
            }
        });

        findViewById(R.id.signInButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithEmail();
            }
        });
    }

    private void signInWithEmail() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(SignInActivity.this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            initializeWebRTCClientAndGoToMain();
                        } else {
                            // ... handle sign in failure
                        }
                    }
                });
    }

    private void signInWithGoogle() {
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign-in failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            initializeWebRTCClientAndGoToMain();
                        } else {
                            Toast.makeText(SignInActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initializeWebRTCClientAndGoToMain() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            MainRepository mainRepository = MainRepository.getInstance();
            mainRepository.initializeWebRTCClient(getApplicationContext(), user.getEmail());

            Intent intent = new Intent(SignInActivity.this, HomeScreen.class);
            startActivity(intent);
            finish();
        }
    }
}