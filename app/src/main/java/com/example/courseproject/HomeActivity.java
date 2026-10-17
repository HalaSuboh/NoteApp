package com.example.courseproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity implements NoteDetailsFragment.communicator {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FloatingActionButton addNote = findViewById(R.id.fabAddNote);
        Toolbar options = findViewById(R.id.toolbar);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navView = findViewById(R.id.navView);
        setSupportActionBar(options);
        options.setTitle("Notey");


        SharedPrefManager sharedPrefManagerForHeader = SharedPrefManager.getInstance(this);
        String loggedInEmail = sharedPrefManagerForHeader.readString(SharedPrefManager.KEY_LOGGED_IN_EMAIL, "");
        TextView navUserEmail = navView.getHeaderView(0).findViewById(R.id.tvNavUserEmail);
        navUserEmail.setText(loggedInEmail);

        options.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        AllNoteFragment allNoteFragment = new AllNoteFragment();
        final FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, allNoteFragment, "AllNotesFrag");
        fragmentTransaction.commit();

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if (id == R.id.nav_all) {

                    AllNoteFragment allNoteFragment = new AllNoteFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, allNoteFragment).commit();

                } else if (id == R.id.nav_favorites) {

                    FavoritesFragment favoritesFragment = new FavoritesFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, favoritesFragment).commit();

                } else if (id == R.id.nav_sorted) {

                    SortedFragment sortedFragment = new SortedFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, sortedFragment).commit();

                } else if (id == R.id.nav_profile) {

                    ProfileFragment profileFragment = new ProfileFragment();
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainer, profileFragment).commit();

                } else if (id == R.id.nav_logout) {

                    SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(HomeActivity.this);
                    sharedPrefManager.logout();

                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AddNewNote.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void respond(int noteId) {
        NoteDetailsFragment fragment = new NoteDetailsFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
        fragment.showNote(noteId);
    }
}