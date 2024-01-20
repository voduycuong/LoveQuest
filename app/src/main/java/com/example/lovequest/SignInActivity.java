package com.example.lovequest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {
    ImageView googleSignInBtn;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase loveQuestDatabase;
    GoogleSignInClient googleSignInClient;
    ProgressDialog progressDialog;
    int RC_SIGN_IN = 40;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        googleSignInBtn = findViewById(R.id.googleSignInBtn);
        firebaseAuth = FirebaseAuth.getInstance();
        loveQuestDatabase = FirebaseDatabase.getInstance();

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

    }

    private void signInWithGoogle() {
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken());

            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void firebaseAuth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Users users = new Users();
                            users.setUserID(user.getUid());
                            users.setName(user.getDisplayName());
                            users.setProfile(user.getPhotoUrl().toString());

                            loveQuestDatabase.getReference().child("Users").child(user.getUid()).setValue(users);

                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(SignInActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}