package com.example.projectpts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity4 extends AppCompatActivity { // BENARKAN: AppCompatActivity

    private EditText etNoteTitle, etNoteContent;
    private Button btnAddNote;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        initViews();
        setupButtonListeners();
        setupBackPressedHandler(); // TAMBAHKAN INI
    }

    private void initViews() {
        etNoteTitle = findViewById(R.id.etNoteTitle);
        etNoteContent = findViewById(R.id.etNoteContent);
        btnAddNote = findViewById(R.id.btnAddNote);
        backButton = findViewById(R.id.backButton);
    }

    private void setupButtonListeners() {
        backButton.setOnClickListener(v -> kembaliKeActivity3());

        btnAddNote.setOnClickListener(v -> simpanDanKembaliKeActivity3());
    }

    // METHOD BARU: Handle back gesture dengan OnBackPressedDispatcher
    private void setupBackPressedHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                kembaliKeActivity3();
            }
        });
    }

    private void simpanDanKembaliKeActivity3() {
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
        kembaliKeActivity3();
    }

    private void simpanCatatan(String judul, String isi) {
        // Implementasi penyimpanan data
        System.out.println("Catatan disimpan - Judul: " + judul + ", Isi: " + isi);
    }

    // BENARKAN: Nama method yang benar
    private void kembaliKeActivity3() {
        Intent intent = new Intent(MainActivity4.this, MainActivity3.class);
        startActivity(intent);
        finish();


    }


    // OPSIONAL: HAPUS method onBackPressed() yang lama
    // @Override
    // public void onBackPressed() {
    //     kembaliKeActivity3();
    // }

}