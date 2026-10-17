package com.example.courseproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.List;

public class FavoritesFragment extends Fragment {

    private LinearLayout notesContainer;
    private LinearLayout emptyState;
    private DataBaseHelper dataBaseHelper;
    private String email;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        notesContainer = view.findViewById(R.id.notesContainer);
        emptyState = view.findViewById(R.id.layoutEmptyState);

        dataBaseHelper = new DataBaseHelper(requireContext());

        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(requireContext());
        email = sharedPrefManager.readString(SharedPrefManager.KEY_LOGGED_IN_EMAIL, "");

        loadFavorites();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (dataBaseHelper != null) {
            loadFavorites();
        }
    }

    private void loadFavorites() {

        List<Note> favoriteNotes = dataBaseHelper.getFavoriteNotes(email);

        notesContainer.removeAllViews();

        displayNotes(favoriteNotes);

        if (favoriteNotes.size() > 0) {
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


            favorite.setImageResource(R.drawable.ic_favorite);
            favorite.setContentDescription("Favorite");
            favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    boolean updated = dataBaseHelper.toggleFavorite(note.getId(), false);

                    if (updated) {
                        FavoriteAnimationHelper.pulse(favorite);
                        favorite.postDelayed(() -> loadFavorites(), 250);
                    }
                }
            });

            final NoteDetailsFragment.communicator communicator =
                    (NoteDetailsFragment.communicator) getActivity();

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    communicator.respond(note.getId());
                }
            });

            notesContainer.addView(row);
        }
    }
}