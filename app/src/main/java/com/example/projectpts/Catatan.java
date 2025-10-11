package com.example.projectpts;

import java.io.Serializable;

public class Catatan implements Serializable {
    private String id;
    private String judul;
    private String isi;
    private long timestamp;
    private boolean completed;

    public Catatan() {
        // Default constructor required for Gson
    }

    public Catatan(String judul, String isi) {
        this.id = String.valueOf(System.currentTimeMillis());
        this.judul = judul;
        this.isi = isi;
        this.timestamp = System.currentTimeMillis();
        this.completed = false;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }

    public String getIsi() { return isi; }
    public void setIsi(String isi) { this.isi = isi; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}