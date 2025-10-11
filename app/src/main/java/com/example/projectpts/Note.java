package com.example.projectpts;

public class Note {
    private String id;
    private String title;
    private String content;
    private String date; // Tambahkan field date

    // Default constructor
    public Note() {
        this.id = String.valueOf(System.currentTimeMillis());
        this.date = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm").format(new java.util.Date());
    }

    // Constructor dengan parameter
    public Note(String title, String content) {
        this.id = String.valueOf(System.currentTimeMillis());
        this.title = title;
        this.content = content;
        this.date = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm").format(new java.util.Date());
    }

    // Constructor dengan semua parameter
    public Note(String id, String title, String content, String date) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    // TAMBAHKAN METHOD INI
    public String getDate() {
        return date;
    }

    // TAMBAHKAN METHOD INI
    public void setDate(String date) {
        this.date = date;
    }
}