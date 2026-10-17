package com.example.courseproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "MyNotes.db";
    private static final int DB_VERSION = 2;

    public DataBaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Carried over from the earlier version: basic SHA-256 hashing so
    // passwords are never stored in plain text.
    // Note: for production you'd also want a per-user salt.
    private static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is always available on Android, so this should never happen.
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE USERS(" +
                        "EMAIL TEXT PRIMARY KEY," +
                        "FIRST_NAME TEXT," +
                        "LAST_NAME TEXT," +
                        "PASSWORD TEXT)"
        );

        db.execSQL(
                "CREATE TABLE NOTES(" +
                        "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "EMAIL TEXT," +
                        "TITLE TEXT," +
                        "CONTENT TEXT," +
                        "TAG TEXT," +
                        "CREATION_DATE TEXT," +
                        "FAVORITE INTEGER DEFAULT 0," +
                        "FOREIGN KEY(EMAIL) REFERENCES USERS(EMAIL))"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS NOTES");
        db.execSQL("DROP TABLE IF EXISTS USERS");
        onCreate(db);
    }

    public boolean insertUser(User user) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("EMAIL", user.getEmail());
        values.put("FIRST_NAME", user.getFirstName());
        values.put("LAST_NAME", user.getLastName());
        values.put("PASSWORD", hashPassword(user.getPassword()));

        long result = db.insert("USERS", null, values);
        return result != -1;
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM USERS WHERE EMAIL = ?",
                new String[]{email}
        );

        User user = null;

        if (cursor.moveToFirst()) {
            user = new User();
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("EMAIL")));
            user.setFirstName(cursor.getString(cursor.getColumnIndexOrThrow("FIRST_NAME")));
            user.setLastName(cursor.getString(cursor.getColumnIndexOrThrow("LAST_NAME")));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow("PASSWORD")));
        }

        cursor.close();
        return user;
    }

    public boolean authenticateUser(String email, String password) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM USERS WHERE EMAIL = ? AND PASSWORD = ?",
                new String[]{email, hashPassword(password)}
        );

        boolean valid = cursor.getCount() > 0;
        cursor.close();
        return valid;
    }

    public boolean updateUserProfile(String currentEmail, String firstName,
                                     String lastName, String newPassword) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("FIRST_NAME", firstName);
        values.put("LAST_NAME", lastName);

        if (newPassword != null && !newPassword.isEmpty()) {
            values.put("PASSWORD", hashPassword(newPassword));
        }

        int rowsAffected = db.update(
                "USERS",
                values,
                "EMAIL = ?",
                new String[]{currentEmail}
        );

        return rowsAffected > 0;
    }


    public long insertNote(Note note) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("EMAIL", note.getEmail());
        values.put("TITLE", note.getTitle());
        values.put("CONTENT", note.getContent());
        values.put("TAG", note.getTag());
        values.put("CREATION_DATE", note.getCreation_date());
        values.put("FAVORITE", note.isFavorite() ? 1 : 0);

        return db.insert("NOTES", null, values);
    }

    public boolean updateNote(Note note) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("TITLE", note.getTitle());
        values.put("CONTENT", note.getContent());
        values.put("TAG", note.getTag());
        values.put("CREATION_DATE", note.getCreation_date());
        values.put("FAVORITE", note.isFavorite() ? 1 : 0);

        int rowsAffected = db.update(
                "NOTES",
                values,
                "ID = ?",
                new String[]{String.valueOf(note.getId())}
        );

        return rowsAffected > 0;
    }

    public boolean deleteNote(int noteId) {
        SQLiteDatabase db = getWritableDatabase();

        int rowsAffected = db.delete(
                "NOTES",
                "ID = ?",
                new String[]{String.valueOf(noteId)}
        );

        return rowsAffected > 0;
    }

    public boolean toggleFavorite(int noteId, boolean isFavorite) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("FAVORITE", isFavorite ? 1 : 0);

        int rowsAffected = db.update(
                "NOTES",
                values,
                "ID = ?",
                new String[]{String.valueOf(noteId)}
        );

        return rowsAffected > 0;
    }

    public List<Note> getNotesByUser(String userEmail) {
        return queryNotes(
                "SELECT * FROM NOTES WHERE EMAIL = ? ORDER BY CREATION_DATE DESC",
                new String[]{userEmail}
        );
    }

    public List<Note> getFavoriteNotes(String userEmail) {
        return queryNotes(
                "SELECT * FROM NOTES WHERE EMAIL = ? AND FAVORITE = 1 ORDER BY CREATION_DATE DESC",
                new String[]{userEmail}
        );
    }

    public List<Note> getSortedNotes(String userEmail, String sortMode) {
        String orderBy = sortMode.equals("alphabetical") ? "TITLE ASC" : "CREATION_DATE DESC";

        return queryNotes(
                "SELECT * FROM NOTES WHERE EMAIL = ? ORDER BY " + orderBy,
                new String[]{userEmail}
        );
    }

    public List<Note> searchNotes(String userEmail, String query) {
        return queryNotes(
                "SELECT * FROM NOTES WHERE EMAIL = ? AND (TITLE LIKE ? OR CONTENT LIKE ?) ORDER BY CREATION_DATE DESC",
                new String[]{userEmail, "%" + query + "%", "%" + query + "%"}
        );
    }

    public List<Note> filterByTag(String userEmail, String tag) {
        return queryNotes(
                "SELECT * FROM NOTES WHERE EMAIL = ? AND TAG = ? ORDER BY CREATION_DATE DESC",
                new String[]{userEmail, tag}
        );
    }

    public List<String> getDistinctTags(String userEmail) {
        List<String> tags = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT DISTINCT TAG FROM NOTES WHERE EMAIL = ? AND TAG IS NOT NULL AND TAG != '' ORDER BY TAG COLLATE NOCASE ASC",
                new String[]{userEmail}
        );

        while (cursor.moveToNext()) {
            tags.add(cursor.getString(0));
        }

        cursor.close();
        return tags;
    }
    public List<Note> getFilteredNotes(String userEmail, String query, String tag, String sortMode) {

        String orderBy = sortMode.equals("alphabetical") ? "TITLE ASC" : "CREATION_DATE DESC";

        StringBuilder sql = new StringBuilder("SELECT * FROM NOTES WHERE EMAIL = ?");
        List<String> args = new ArrayList<>();
        args.add(userEmail);

        if (query != null && !query.isEmpty()) {
            sql.append(" AND (TITLE LIKE ? OR CONTENT LIKE ?)");
            args.add("%" + query + "%");
            args.add("%" + query + "%");
        }

        if (tag != null && !tag.equals("All Tags")) {
            sql.append(" AND TAG = ?");
            args.add(tag);
        }

        sql.append(" ORDER BY ").append(orderBy);

        return queryNotes(sql.toString(), args.toArray(new String[0]));
    }

    private List<Note> queryNotes(String sql, String[] args) {
        List<Note> notesList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, args);

        while (cursor.moveToNext()) {
            Note note = new Note();
            note.setId(cursor.getInt(cursor.getColumnIndexOrThrow("ID")));
            note.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("EMAIL")));
            note.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("TITLE")));
            note.setContent(cursor.getString(cursor.getColumnIndexOrThrow("CONTENT")));
            note.setTag(cursor.getString(cursor.getColumnIndexOrThrow("TAG")));
            note.setCreation_date(cursor.getString(cursor.getColumnIndexOrThrow("CREATION_DATE")));
            note.setFavorite(cursor.getInt(cursor.getColumnIndexOrThrow("FAVORITE")) == 1);
            notesList.add(note);
        }

        cursor.close();
        return notesList;
    }
}