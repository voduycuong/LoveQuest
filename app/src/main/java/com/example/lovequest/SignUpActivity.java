package com.example.lovequest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {
    private ImageView googleSignInBtn;
    private EditText emailEditText, passwordEditText, confirmEmailEditText, confirmPasswordEditText;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private ProgressDialog progressDialog;
    private final int RC_SIGN_IN = 40;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        googleSignInBtn = findViewById(R.id.googleSignInBtn);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmEmailEditText = findViewById(R.id.confirmEmailEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);

        firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("815785697369-h01ujjrou2ora4n5dmtj5dugg5ot0udr.apps.googleusercontent.com")
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

        findViewById(R.id.signUpButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccountWithEmail();
                // Start sign in activity
                Intent intent = new Intent(SignUpActivity.this, BeginActivity1.class);
                startActivity(intent);
                finish();
            }

        });
    }

    private void createAccountWithEmail() {
        String email = emailEditText.getText().toString();
        String confirmEmail = confirmEmailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        if (!email.equals(confirmEmail) || !password.equals(confirmPassword)) {
            Toast.makeText(SignUpActivity.this, "Emails or passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Add more robust validation for email and password

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUserProfile(user);
                        } else {
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
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
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUserProfile(user);
                        } else {
                            Toast.makeText(SignUpActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateUserProfile(FirebaseUser user) {
        if (user != null) {
            UserModel userModel = new UserModel();
            userModel.setUserId(user.getUid());
            userModel.setUsername(user.getDisplayName());
            userModel.setPhotoUrl(user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null);

            FirebaseFirestore.getInstance().collection("Users").document(user.getUid()).set(userModel)
                    .addOnSuccessListener(aVoid -> {
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(SignUpActivity.this, "Error saving user information", Toast.LENGTH_SHORT).show());
        }
    }
}
