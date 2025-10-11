package com.example.projectpts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity4 extends AppCompatActivity {

    private EditText etNoteTitle, etNoteContent;
    private Button btnAddNote;
    private ImageButton backButton;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    // HARUS SAMA dengan MainActivity8
    private static final String PREFS_NAME = "NotesPrefs";
    private static final String NOTES_KEY = "notes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        initViews();
        setupSharedPreferences();
        setupButtonListeners();
    }

    private void initViews() {
        etNoteTitle = findViewById(R.id.etNoteTitle);
        etNoteContent = findViewById(R.id.etNoteContent);
        btnAddNote = findViewById(R.id.btnAddNote);
        backButton = findViewById(R.id.backButton);
    }

    private void setupSharedPreferences() {
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        gson = new Gson();
    }

    private void setupButtonListeners() {
        backButton.setOnClickListener(v -> kembaliKeMainActivity8());

        btnAddNote.setOnClickListener(v -> simpanDanKembaliKeActivity8());
    }

    private void simpanDanKembaliKeActivity8() {
        String judul = etNoteTitle.getText().toString().trim();
        String isi = etNoteContent.getText().toString().trim();

        if (judul.isEmpty()) {
            Toast.makeText(this, "Judul catatan tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            etNoteTitle.requestFocus();
            return;
        }

        if (isi.isEmpty()) {
            Toast.makeText(this, "Isi catatan tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            etNoteContent.requestFocus();
            return;
        }

        simpanCatatan(judul, isi);
        Toast.makeText(this, "Catatan berhasil disimpan!", Toast.LENGTH_SHORT).show();

        // Kembali ke MainActivity8 (Notes list)
        Intent intent = new Intent(MainActivity4.this, MainActivity8.class);
        startActivity(intent);
        finish();
    }

    private void simpanCatatan(String judul, String isi) {
        // Buat objek Note baru
        Note noteBaru = new Note(judul, isi);

        // Load notes yang sudah ada
        List<Note> noteList = loadNoteList();

        // Tambahkan note baru
        noteList.add(noteBaru);

        // Simpan kembali ke SharedPreferences
        saveNoteList(noteList);
    }

    private List<Note> loadNoteList() {
        String notesJson = sharedPreferences.getString(NOTES_KEY, "");
        if (notesJson.isEmpty()) {
            return new ArrayList<>();
        }

        Type type = new TypeToken<List<Note>>(){}.getType();
        List<Note> noteList = gson.fromJson(notesJson, type);
        return noteList != null ? noteList : new ArrayList<>();
    }

    private void saveNoteList(List<Note> noteList) {
        String notesJson = gson.toJson(noteList);
        sharedPreferences.edit().putString(NOTES_KEY, notesJson).apply();
    }

    private void kembaliKeMainActivity8() {
        Intent intent = new Intent(MainActivity4.this, MainActivity8.class);
        startActivity(intent);
        finish();
    }
}