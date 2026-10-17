package com.example.courseproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Date;

public class AddNewNote extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_note);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        EditText title = findViewById(R.id.etNoteTitle);
        EditText content = findViewById(R.id.etNoteContent);
        EditText tag = findViewById(R.id.etNoteTag);
        TextView date = findViewById(R.id.tvNoteDate);
        Button save = findViewById(R.id.btnSaveNote);

        DataBaseHelper dataBaseHelper = new DataBaseHelper(AddNewNote.this);
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(this);

        Date creationDate = new Date();
        date.setText(NoteDateFormatter.forDisplay(creationDate.toString()));

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = sharedPrefManager.readString(SharedPrefManager.KEY_LOGGED_IN_EMAIL, "");
                String titleText = title.getText().toString().trim();
                String contentText = content.getText().toString().trim();
                String tagText = tag.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(AddNewNote.this, "User not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (titleText.isEmpty()) {
                    title.setError("Title is required");
                    title.requestFocus();
                    return;
                }

                if (contentText.isEmpty()) {
                    content.setError("Content is required");
                    content.requestFocus();
                    return;
                }

                Note newNote = new Note();
                newNote.setEmail(email);
                newNote.setTitle(titleText);
                newNote.setContent(contentText);
                newNote.setTag(tagText);
                newNote.setCreation_date(creationDate.toString());
                newNote.setFavorite(false);

                long noteId = dataBaseHelper.insertNote(newNote);

                if (noteId != -1) {
                    Toast.makeText(AddNewNote.this, "Note saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddNewNote.this, "Failed to save note", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}