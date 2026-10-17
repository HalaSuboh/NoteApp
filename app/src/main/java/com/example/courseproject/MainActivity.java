package com.example.courseproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText email = findViewById(R.id.etEmail);
        EditText password = findViewById(R.id.etPassword);
        Button signIN = findViewById(R.id.btnSignIn);
        Button signUP = findViewById(R.id.btnSignUp);
        CheckBox remember = findViewById(R.id.cbRememberMe);

        DataBaseHelper dataBaseHelper = new DataBaseHelper(MainActivity.this);
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(this);

        boolean savedRemember = sharedPrefManager.readBoolean(SharedPrefManager.KEY_REMEMBER_ME, false);
        remember.setChecked(savedRemember);
        if (savedRemember) {
            String savedEmail = sharedPrefManager.readString(SharedPrefManager.KEY_REMEMBERED_EMAIL, "");
            email.setText(savedEmail);
        }

        signIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailText = email.getText().toString().trim();
                String passwordText = password.getText().toString();

                if (dataBaseHelper.authenticateUser(emailText, passwordText)) {


                    sharedPrefManager.writeString(SharedPrefManager.KEY_LOGGED_IN_EMAIL, emailText);

                    sharedPrefManager.writeBoolean(SharedPrefManager.KEY_REMEMBER_ME, remember.isChecked());
                    if (remember.isChecked()) {
                        sharedPrefManager.writeString(SharedPrefManager.KEY_REMEMBERED_EMAIL, emailText);
                    }

                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(MainActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUp.class);
                startActivity(intent);
            }
        });
    }
}