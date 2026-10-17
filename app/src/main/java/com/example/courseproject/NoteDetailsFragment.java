package com.example.courseproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

public class NoteDetailsFragment extends Fragment {

    private int noteId;
    private EditText etDetailTitle;
    private TextView tvDetailTitleError;
    private TextView tvDetailDate;
    private EditText etDetailTag;
    private EditText etDetailContent;
    private ImageButton btnBack;
    private Button btnSaveNote;
    private ImageButton btnDelete;
    private ImageView ivFavoriteToggle;
    private Note currentNote;
    private ImageButton btnEmail;

    public NoteDetailsFragment() {
        super(R.layout.fragment_note_details);
    }

    public interface communicator {
        void respond(int noteId);
    }

    public void showNote(int noteId) {
        this.noteId = noteId;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etDetailTitle = view.findViewById(R.id.etDetailTitle);
        tvDetailTitleError = view.findViewById(R.id.tvDetailTitleError);
        tvDetailDate = view.findViewById(R.id.tvDetailDate);
        etDetailTag = view.findViewById(R.id.etDetailTag);
        etDetailContent = view.findViewById(R.id.etDetailContent);
        btnSaveNote = view.findViewById(R.id.btnSaveNote);
        btnBack = view.findViewById(R.id.btnBack);
        btnDelete = view.findViewById(R.id.btnDeleteNote);
        ivFavoriteToggle = view.findViewById(R.id.ivFavoriteToggle);
        btnEmail = view.findViewById(R.id.btnEmailNote);

        boolean noteIsFound = false;
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getContext());
        String email = sharedPrefManager.readString(SharedPrefManager.KEY_LOGGED_IN_EMAIL, "");
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getContext());
        List<Note> userNotes = dataBaseHelper.getNotesByUser(email);

        for (int i = 0; i < userNotes.size(); i++) {
            if (userNotes.get(i).getId() == noteId) {
                currentNote = userNotes.get(i);
                noteIsFound = true;
                break;
            }
        }

        if (!noteIsFound) {
            Toast.makeText(getContext(), "Note is not found", Toast.LENGTH_SHORT).show();
        }

        if (currentNote != null) {
            etDetailTitle.setText(currentNote.getTitle());
            tvDetailDate.setText(NoteDateFormatter.forDisplay(currentNote.getCreation_date()));
            etDetailTag.setText(currentNote.getTag());
            etDetailContent.setText(currentNote.getContent());

            if (currentNote.isFavorite()) {
                ivFavoriteToggle.setImageResource(R.drawable.ic_favorite);
            } else {
                ivFavoriteToggle.setImageResource(R.drawable.ic_favorite_border);
            }
        }

        btnSaveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentNote == null) {
                    return;
                }

                String titleText = etDetailTitle.getText().toString().trim();
                if (titleText.isEmpty()) {
                    tvDetailTitleError.setVisibility(View.VISIBLE);
                    etDetailTitle.requestFocus();
                    return;
                }
                tvDetailTitleError.setVisibility(View.GONE);

                currentNote.setTitle(titleText);
                currentNote.setTag(etDetailTag.getText().toString());
                currentNote.setContent(etDetailContent.getText().toString());
                boolean updated = dataBaseHelper.updateNote(currentNote);

                if (updated) {
                    Toast.makeText(getContext(), "Note updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean deleted = dataBaseHelper.deleteNote(noteId);

                if (deleted) {
                    Toast.makeText(getContext(), "Note deleted successfully", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getContext(), "Delete failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ivFavoriteToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentNote == null) {
                    return;
                }
                boolean newFavoriteState = !currentNote.isFavorite();
                boolean updated = dataBaseHelper.toggleFavorite(noteId, newFavoriteState);
                if (updated) {
                    currentNote.setFavorite(newFavoriteState);
                    if (currentNote.isFavorite()) {
                        ivFavoriteToggle.setImageResource(R.drawable.ic_favorite);
                    } else {
                        ivFavoriteToggle.setImageResource(R.drawable.ic_favorite_border);
                    }
                }
            }
        });

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentNote == null) {
                    return;
                }
                Intent gmailIntent = new Intent();
                gmailIntent.setAction(Intent.ACTION_SENDTO);
                gmailIntent.setData(Uri.parse("mailto:"));
                gmailIntent.putExtra(Intent.EXTRA_SUBJECT, currentNote.getTitle());
                gmailIntent.putExtra(Intent.EXTRA_TEXT, currentNote.getContent());
                startActivity(gmailIntent);
            }
        });
    }
}