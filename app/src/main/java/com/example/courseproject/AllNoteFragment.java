package com.example.courseproject;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class AllNoteFragment extends Fragment {

    private String currentSortMode = "date";
    private String currentTag = "All Tags";
    private String currentQuery = "";

    private DataBaseHelper dataBaseHelper;
    private SharedPrefManager sharedPrefManager;
    private String email;

    private LinearLayout notesContainer;
    private LinearLayout emptyState;
    private Spinner spinnerSort;
    private Spinner spinnerTag;
    private ArrayAdapter<String> tagAdapter;
    private boolean suppressTagCallback = false;

    public AllNoteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_all_note, container, false);

        EditText etSearch = view.findViewById(R.id.etSearch);
        notesContainer = view.findViewById(R.id.notesContainer);
        emptyState = view.findViewById(R.id.layoutEmptyState);
        spinnerSort = view.findViewById(R.id.spinnerSort);
        spinnerTag = view.findViewById(R.id.spinnerTag);


        String[] sortOptions = {"Sort by...", "Alphabetically", "Creation Date"};
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item_selected, sortOptions);
        sortAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerSort.setAdapter(sortAdapter);

        dataBaseHelper = new DataBaseHelper(requireContext());
        sharedPrefManager = SharedPrefManager.getInstance(requireContext());
        email = sharedPrefManager.readString(SharedPrefManager.KEY_LOGGED_IN_EMAIL, "");

        currentSortMode = sharedPrefManager.readString(SharedPrefManager.KEY_SORT_MODE, "date");


        if (currentSortMode.equals("alphabetical")) {
            spinnerSort.setSelection(1);
        } else {
            spinnerSort.setSelection(2);
        }

        refreshTagSpinner();
        loadNotes();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                currentQuery = s.toString().trim();
                loadNotes();
            }
        });

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 1) {
                    currentSortMode = "alphabetical";
                    sharedPrefManager.writeString(SharedPrefManager.KEY_SORT_MODE, "alphabetical");
                    loadNotes();
                } else if (position == 2) {
                    currentSortMode = "date";
                    sharedPrefManager.writeString(SharedPrefManager.KEY_SORT_MODE, "date");
                    loadNotes();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerTag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (suppressTagCallback) {
                    return;
                }
                currentTag = (String) spinnerTag.getSelectedItem();
                loadNotes();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (dataBaseHelper != null) {
            refreshTagSpinner();
            loadNotes();
        }
    }


    private void refreshTagSpinner() {

        List<String> tags = new ArrayList<>();
        tags.add("All Tags");
        tags.addAll(dataBaseHelper.getDistinctTags(email));

        tagAdapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item_selected, tags);
        tagAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerTag.setAdapter(tagAdapter);

        int position = tags.indexOf(currentTag);
        if (position < 0) {
            currentTag = "All Tags";
            position = 0;
        }

        suppressTagCallback = true;
        spinnerTag.setSelection(position);
        suppressTagCallback = false;
    }

    private void loadNotes() {

        List<Note> notes = dataBaseHelper.getFilteredNotes(email, currentQuery, currentTag, currentSortMode);

        notesContainer.removeAllViews();

        displayNotes(notes);

        if (notes.size() > 0) {
            emptyState.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.VISIBLE);
        }
    }

    private void displayNotes(List<Note> notes) {

        LayoutInflater inflater = LayoutInflater.from(requireContext());

        for (Note note : notes) {

            View row = inflater.inflate(R.layout.item_note, notesContainer, false);

            TextView title = row.findViewById(R.id.tvNoteItemTitle);
            TextView snippet = row.findViewById(R.id.tvNoteItemSnippet);
            TextView tag = row.findViewById(R.id.tvNoteItemTag);
            TextView date = row.findViewById(R.id.tvNoteItemDate);
            ImageView favorite = row.findViewById(R.id.ivNoteItemFavorite);

            title.setText(note.getTitle());
            snippet.setVisibility(View.GONE);
            date.setText(NoteDateFormatter.forDisplay(note.getCreation_date()));

            if (note.getTag() == null || note.getTag().trim().isEmpty()) {
                tag.setVisibility(View.GONE);
            } else {
                tag.setVisibility(View.VISIBLE);
                tag.setText(note.getTag());
            }

            favorite.setImageResource(note.isFavorite() ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
            favorite.setContentDescription("Favorite");

            final NoteDetailsFragment.communicator communicator = (NoteDetailsFragment.communicator) getActivity();

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    communicator.respond(note.getId());
                }
            });
            favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean newFavoriteState = !note.isFavorite();
                    boolean updated = dataBaseHelper.toggleFavorite(note.getId(), newFavoriteState);

                    if (updated) {
                        note.setFavorite(newFavoriteState);
                        favorite.setImageResource(note.isFavorite() ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
                        FavoriteAnimationHelper.pulse(favorite);
                    }
                }
            });

            notesContainer.addView(row);
        }
    }
}