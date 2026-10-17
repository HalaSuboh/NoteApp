package com.example.courseproject;

public class Note {
    private int id;

    private String email;
    private String title;
    private String content;
    private String creation_date;
    private String tag;
    private boolean favorite;

    public Note(int id, String email, String title, String content, String creation_date, String tag, boolean favorite) {
        this.id = id;
        this.email = email;
        this.title = title;
        this.content = content;
        this.creation_date = creation_date;
        this.tag = tag;
        this.favorite = favorite;
    }

    public Note() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(String creation_date) {
        this.creation_date = creation_date;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}