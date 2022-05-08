package com.example.cinemaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();
    private static final int RC_SIGN_IN = 123;
    private static final String SECRET_KEY = "e5e9fa1ba31ecd1ae84f75caaa474f3a663f05f4";

    EditText loginEmailEditText;
    EditText loginPasswordEditText;

    private SharedPreferences preferences;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginEmailEditText = findViewById(R.id.loginEmailEditText);
        loginPasswordEditText = findViewById(R.id.loginPasswordEditText);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("338222723662-k4rl1ra76t0brhnhd21ndggtf030531a.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    public void loginAnimation() {
        Animation slide_animation = AnimationUtils.loadAnimation(this, R.anim.slide_animation);

        TextView uTextView = findViewById(R.id.loginEmailEditText);
        TextView pTextView = findViewById(R.id.loginPasswordEditText);

        uTextView.startAnimation(slide_animation);
        pTextView.startAnimation(slide_animation);
    }

    public void login(View view) {
        String emailAddress = loginEmailEditText.getText().toString();
        String password = loginPasswordEditText.getText().toString();

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.shake_animation);


        mAuth.signInWithEmailAndPassword(emailAddress, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                loginAnimation();
                Log.d(LOG_TAG, emailAddress + " log in successful");
                Toast.makeText(MainActivity.this, "Login succesful", Toast.LENGTH_LONG).show();
                browseMovies();
            } else {
                Log.d(LOG_TAG, emailAddress + " failed to log in");
                Button button = findViewById(R.id.loginButton);
                button.startAnimation(animation);
                Toast.makeText(MainActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(MainActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    private void browseMovies() {
        Intent intent = new Intent(this, MovieSelectionActivity.class);
        startActivity(intent);
    }

    public void loginAsGuest(View view) {
        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    loginAnimation();
                    Log.d(LOG_TAG, "Guest user logged in successfully");
                    Toast.makeText(MainActivity.this, "Login succesful", Toast.LENGTH_LONG).show();
                    browseMovies();
                } else {
                    Log.d(LOG_TAG, "Guest user failed to log in.");
                    Toast.makeText(MainActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void loginWithGoogle(View view) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userName", loginEmailEditText.getText().toString());
        editor.putString("password", loginPasswordEditText.getText().toString());
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG, "onRestart");
    }
}