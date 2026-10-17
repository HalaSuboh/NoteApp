package com.example.courseproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private EditText etFirstName;
    private EditText etLastName;
    private EditText etNewPassword;
    private Button btnSaveProfile;

    private DataBaseHelper dataBaseHelper;
    private SharedPrefManager sharedPrefManager;
    private String email;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        etFirstName = view.findViewById(R.id.etProfileFirstName);
        etLastName = view.findViewById(R.id.etProfileLastName);
        etNewPassword = view.findViewById(R.id.etProfileNewPassword);
        btnSaveProfile = view.findViewById(R.id.btnSaveProfile);

        dataBaseHelper = new DataBaseHelper(requireContext());
        sharedPrefManager = SharedPrefManager.getInstance(requireContext());
        email = sharedPrefManager.readString(SharedPrefManager.KEY_LOGGED_IN_EMAIL, "");

        loadCurrentUser();

        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCurrentUser();
    }

    private void loadCurrentUser() {

        if (email.isEmpty()) {
            Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        User currentUser = dataBaseHelper.getUserByEmail(email);

        if (currentUser != null) {
            etFirstName.setText(currentUser.getFirstName());
            etLastName.setText(currentUser.getLastName());

        } else {
            Toast.makeText(getContext(), "Could not load profile", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProfile() {

        String firstNameText = etFirstName.getText().toString().trim();
        String lastNameText = etLastName.getText().toString().trim();
        String newPasswordText = etNewPassword.getText().toString();

        if (firstNameText.isEmpty()) {
            etFirstName.setError("First name is required");
            etFirstName.requestFocus();
            return;
        }

        if (firstNameText.length() < 3 || firstNameText.length() > 10) {
            etFirstName.setError("Must be 3–10 characters");
            etFirstName.requestFocus();
            return;
        }

        if (lastNameText.isEmpty()) {
            etLastName.setError("Last name is required");
            etLastName.requestFocus();
            return;
        }

        if (lastNameText.length() < 3 || lastNameText.length() > 10) {
            etLastName.setError("Must be 3–10 characters");
            etLastName.requestFocus();
            return;
        }


        if (!newPasswordText.isEmpty()) {

            if (newPasswordText.length() < 6 || newPasswordText.length() > 12) {
                etNewPassword.setError("Password must be 6–12 characters");
                etNewPassword.requestFocus();
                return;
            }

            boolean containsDigit = false;
            boolean containsUpper = false;
            boolean containsLower = false;

            for (int i = 0; i < newPasswordText.length(); i++) {
                char c = newPasswordText.charAt(i);
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
                etNewPassword.setError("6–12 chars, incl. 1 upper, 1 lower, 1 number");
                etNewPassword.requestFocus();
                return;
            }
        }

        boolean updated = dataBaseHelper.updateUserProfile(email, firstNameText, lastNameText, newPasswordText);

        if (updated) {
            Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
            etNewPassword.setText("");
        } else {
            Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show();
        }
    }
}