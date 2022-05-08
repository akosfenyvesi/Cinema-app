package com.example.cinemaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final String PREF_KEY = RegisterActivity.class.getPackage().toString();
    private static final String SECRET_KEY = "e5e9fa1ba31ecd1ae84f75caaa474f3a663f05f4";

    private SharedPreferences preferences;
    private FirebaseAuth mAuth;

    EditText userEmailEditText;
    EditText passwordEditText;
    EditText passwordRepeatEditText;
    EditText firstNameEditText;
    EditText lastNameEditText;
    Spinner citySpinner;
    DatePicker dateOfBirthDatePicker;
    RadioGroup discountRadioGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        String secret_key = getIntent().getStringExtra("SECRET_KEY");

        if (!secret_key.equals(SECRET_KEY)) {
            finish();
        }

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);

        userEmailEditText = findViewById(R.id.userEmailEditText);
        String userEmail = preferences.getString("userName", "");
        userEmailEditText.setText(userEmail);

        passwordEditText = findViewById(R.id.passwordEditText);
        String password = preferences.getString("password", "");
        passwordEditText.setText(password);

        passwordRepeatEditText = findViewById(R.id.passwordRepeatEditText);
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);

        citySpinner = findViewById(R.id.citySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.cities_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(adapter);

        dateOfBirthDatePicker = findViewById(R.id.dateOfBirthDatePicker);
        discountRadioGroup = findViewById(R.id.discountRadioGroup);
        discountRadioGroup.check(R.id.normalRadioButton);

        mAuth = FirebaseAuth.getInstance();

        Log.i(LOG_TAG, "onCreate");
    }

    public void registration(View view) {
        String userEmail = userEmailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String passwordRepeated = passwordRepeatEditText.getText().toString();

        if (!password.equals(passwordRepeated)) {
            Log.e(LOG_TAG, "The passwords should match!");
            Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
            return;
        }

        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String city = citySpinner.getSelectedItem().toString();
        String dateOfBirth = dateOfBirthDatePicker.getYear() + "/" + dateOfBirthDatePicker.getMonth() + "/" + dateOfBirthDatePicker.getDayOfMonth();
        int discountId = discountRadioGroup.getCheckedRadioButtonId();
        View radioButton = discountRadioGroup.findViewById(discountId);
        int id = discountRadioGroup.indexOfChild(radioButton);
        String discountType = ((RadioButton)discountRadioGroup.getChildAt(id)).getText().toString();

        Log.d(LOG_TAG, userEmail + password);

        mAuth.createUserWithEmailAndPassword(userEmail, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(LOG_TAG, "User created successfully");
                    Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_LONG).show();
                    browseMovies();
                } else {
                    Log.d(LOG_TAG, "User registration failed");
                    Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void cancel(View view) { finish(); }

    public void browseMovies(/* TODO: registered user class */) {
        Intent intent = new Intent(this, MovieSelectionActivity.class);
        startActivity(intent);
    }
}
