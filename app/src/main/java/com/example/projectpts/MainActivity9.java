package com.example.projectpts;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity9 extends AppCompatActivity {

    private TextView tvNoteTitle, tvNoteContent, tvNoteDate;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main9);

        initViews();
        setupBackButton();
        loadNoteData();
    }

    private void initViews() {
        tvNoteTitle = findViewById(R.id.tvNoteTitle);
        tvNoteContent = findViewById(R.id.tvNoteContent);
        tvNoteDate = findViewById(R.id.tvNoteDate);
        backButton = findViewById(R.id.backButton);
    }

    private void setupBackButton() {
        backButton.setOnClickListener(v -> {
            // Kembali ke MainActivity8
            Intent intent = new Intent(MainActivity9.this, MainActivity8.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadNoteData() {
        // Ambil data dari intent
        Intent intent = getIntent();
        if (intent != null) {
            String noteTitle = intent.getStringExtra("NOTE_TITLE");
            String noteContent = intent.getStringExtra("NOTE_CONTENT");
            String noteDate = intent.getStringExtra("NOTE_DATE");

            // Set data ke views
            if (noteTitle != null) {
                tvNoteTitle.setText(noteTitle);
            }
            if (noteContent != null) {
                tvNoteContent.setText(noteContent);
            }
            if (noteDate != null) {
                tvNoteDate.setText(noteDate);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Handle back button pressed
        Intent intent = new Intent(MainActivity9.this, MainActivity8.class);
        startActivity(intent);
        finish();
    }
}