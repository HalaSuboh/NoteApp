package com.example.courseproject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        EditText email = findViewById(R.id.etEmail);
        EditText firstName = findViewById(R.id.etFirstName);
        EditText lastName = findViewById(R.id.etLastName);
        EditText password = findViewById(R.id.etPassword);
        EditText confirmPassword = findViewById(R.id.etConfirmPassword);

        TextView emailError = findViewById(R.id.tvEmailError);
        TextView firstNameError = findViewById(R.id.tvFirstNameError);
        TextView lastNameError = findViewById(R.id.tvLastNameError);
        TextView passwordError = findViewById(R.id.tvPasswordError);
        TextView confirmPasswordError = findViewById(R.id.tvConfirmPasswordError);

        ImageView emailStep = findViewById(R.id.imgEmailStep);
        ImageView firstNameStep = findViewById(R.id.imgFirstNameStep);
        ImageView lastNameStep = findViewById(R.id.imgLastNameStep);
        ImageView passwordStep = findViewById(R.id.imgPasswordStep);
        ImageView confirmPasswordStep = findViewById(R.id.imgConfirmPasswordStep);

        Button createAccount = findViewById(R.id.btnCreateAccount);


        TextView goToSignIn = findViewById(R.id.tvGoToSignIn);

        DataBaseHelper dataBaseHelper = new DataBaseHelper(SignUp.this);
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(this);

        goToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                startActivity(new Intent(SignUp.this, MainActivity.class));
            }
        });

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean valid = true;

                String emailText = email.getText().toString().trim();
                String firstNameText = firstName.getText().toString().trim();
                String lastNameText = lastName.getText().toString().trim();
                String passwordText = password.getText().toString();
                String confirmPasswordText = confirmPassword.getText().toString();

                if (emailText.isEmpty()) {
                    emailError.setText("Email is required");
                    emailError.setVisibility(View.VISIBLE);
                    email.setBackgroundColor(Color.rgb(255, 220, 220));
                    emailStep.setImageResource(R.drawable.progress_empty);
                    valid = false;

                } else if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                    emailError.setText("Enter a valid email address");
                    emailError.setVisibility(View.VISIBLE);
                    email.setBackgroundColor(Color.rgb(255, 220, 220));
                    emailStep.setImageResource(R.drawable.progress_empty);
                    valid = false;

                } else {
                    emailError.setVisibility(View.GONE);
                    email.setBackgroundResource(R.drawable.edittext_background);
                    emailStep.setImageResource(R.drawable.progress_complete);
                }

                if (firstNameText.isEmpty()) {
                    firstNameError.setText("First name is required");
                    firstNameError.setVisibility(View.VISIBLE);
                    firstName.setBackgroundColor(Color.rgb(255, 220, 220));
                    firstNameStep.setImageResource(R.drawable.progress_empty);
                    valid = false;

                } else if (firstNameText.length() < 3 || firstNameText.length() > 10) {
                    firstNameError.setText("Must be 3–10 characters");
                    firstNameError.setVisibility(View.VISIBLE);
                    firstName.setBackgroundColor(Color.rgb(255, 220, 220));
                    firstNameStep.setImageResource(R.drawable.progress_empty);
                    valid = false;

                } else {
                    firstNameError.setVisibility(View.GONE);
                    firstName.setBackgroundResource(R.drawable.edittext_background);
                    firstNameStep.setImageResource(R.drawable.progress_complete);
                }

                if (lastNameText.isEmpty()) {
                    lastNameError.setText("Last name is required");
                    lastNameError.setVisibility(View.VISIBLE);
                    lastName.setBackgroundColor(Color.rgb(255, 220, 220));
                    lastNameStep.setImageResource(R.drawable.progress_empty);
                    valid = false;

                } else if (lastNameText.length() < 3 || lastNameText.length() > 10) {
                    lastNameError.setText("Must be 3–10 characters");
                    lastNameError.setVisibility(View.VISIBLE);
                    lastName.setBackgroundColor(Color.rgb(255, 220, 220));
                    lastNameStep.setImageResource(R.drawable.progress_empty);
                    valid = false;

                } else {
                    lastNameError.setVisibility(View.GONE);
                    lastName.setBackgroundResource(R.drawable.edittext_background);
                    lastNameStep.setImageResource(R.drawable.progress_complete);
                }

                if (passwordText.isEmpty()) {
                    passwordError.setText("Password is required");
                    passwordError.setVisibility(View.VISIBLE);
                    password.setBackgroundColor(Color.rgb(255, 220, 220));
                    passwordStep.setImageResource(R.drawable.progress_empty);
                    valid = false;

                } else if (passwordText.length() < 6 || passwordText.length() > 12) {
                    passwordError.setText("Password must be 6–12 characters");
                    passwordError.setVisibility(View.VISIBLE);
                    password.setBackgroundColor(Color.rgb(255, 220, 220));
                    passwordStep.setImageResource(R.drawable.progress_empty);
                    valid = false;

                } else {

                    boolean containsDigit = false;
                    boolean containsUpper = false;
                    boolean containsLower = false;

                    for (int i = 0; i < passwordText.length(); i++) {
                        char c = passwordText.charAt(i);
                        if (Character.isDigit(c)) {
                            containsDigit = true;
                        }
                        if (Character.isUpperCase(c)) {
                            containsUpper = true;
                        }
                        if (Character.isLowerCase(c)) {
                            containsLower = true;
                        }
                    }

                    if (!containsDigit || !containsUpper || !containsLower) {
                        passwordError.setText("6–12 chars, incl. 1 upper, 1 lower, 1 number");
                        passwordError.setVisibility(View.VISIBLE);
                        password.setBackgroundColor(Color.rgb(255, 220, 220));
                        passwordStep.setImageResource(R.drawable.progress_empty);
                        valid = false;

                    } else {
                        passwordError.setVisibility(View.GONE);
                        password.setBackgroundResource(R.drawable.edittext_background);
                        passwordStep.setImageResource(R.drawable.progress_complete);
                    }
                }

                if (confirmPasswordText.isEmpty()) {
                    confirmPasswordError.setText("Confirm password is required");
                    confirmPasswordError.setVisibility(View.VISIBLE);
                    confirmPassword.setBackgroundColor(Color.rgb(255, 220, 220));
                    confirmPasswordStep.setImageResource(R.drawable.progress_empty);
                    valid = false;

                } else if (!confirmPasswordText.equals(passwordText)) {
                    confirmPasswordError.setText("Passwords do not match");
                    confirmPasswordError.setVisibility(View.VISIBLE);
                    confirmPassword.setBackgroundColor(Color.rgb(255, 220, 220));
                    confirmPasswordStep.setImageResource(R.drawable.progress_empty);
                    valid = false;

                } else {
                    confirmPasswordError.setVisibility(View.GONE);
                    confirmPassword.setBackgroundResource(R.drawable.edittext_background);
                    confirmPasswordStep.setImageResource(R.drawable.progress_complete);
                }

                if (!valid) {
                    return;
                }

                emailError.setVisibility(View.GONE);
                firstNameError.setVisibility(View.GONE);
                lastNameError.setVisibility(View.GONE);
                passwordError.setVisibility(View.GONE);
                confirmPasswordError.setVisibility(View.GONE);


                if (dataBaseHelper.getUserByEmail(emailText) != null) {
                    emailError.setText("An account with this email already exists");
                    emailError.setVisibility(View.VISIBLE);
                    email.setBackgroundColor(Color.rgb(255, 220, 220));
                    emailStep.setImageResource(R.drawable.progress_empty);
                    return;
                }

                User newUser = new User(emailText, firstNameText, lastNameText, passwordText);
                boolean inserted = dataBaseHelper.insertUser(newUser);

                if (!inserted) {
                    Toast.makeText(SignUp.this, "Sign up failed, please try again", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Prevent a second tap while the cat is dancing
                createAccount.setEnabled(false);

                View dimScrim = findViewById(R.id.viewSignUpDim);
                dimScrim.setVisibility(View.VISIBLE);
                dimScrim.setAlpha(0f);
                dimScrim.animate().alpha(1f).setDuration(250).start();

                ImageView dancingCat = findViewById(R.id.imgDancingCat);
                dancingCat.setVisibility(View.VISIBLE);

                AnimationDrawable catDance = (AnimationDrawable) dancingCat.getDrawable();

                dancingCat.post(catDance::start);


                Animation growAnimation = AnimationUtils.loadAnimation(SignUp.this, R.anim.cat_grow);
                dancingCat.startAnimation(growAnimation);

                sharedPrefManager.writeString(SharedPrefManager.KEY_LOGGED_IN_EMAIL, emailText);


                new Handler().postDelayed(() -> {
                    catDance.stop();
                    Intent intent = new Intent(SignUp.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }, 1600);
            }
        });
    }
}