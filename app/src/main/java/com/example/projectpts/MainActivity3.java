package com.example.projectpts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        setupButtons();
        setupFooterNavigation();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(MainActivity3.this, MainActivity2.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setupButtons() {
        try {
            // Setup ADD TASK button
            LinearLayout btnAddTask = findViewById(R.id.btnAddTask);
            if (btnAddTask != null) {
                btnAddTask.setOnClickListener(v -> {
                    Toast.makeText(MainActivity3.this, "ADD TASK Clicked", Toast.LENGTH_SHORT).show();
                    // Navigate to Add Task activity jika ada
                    // Intent intent = new Intent(MainActivity3.this, MainActivity4.class);
                    // startActivity(intent);
                });
            }

            // Setup VIEW TASK button
            LinearLayout btnViewTask = findViewById(R.id.btnViewTask);
            if (btnViewTask != null) {
                btnViewTask.setOnClickListener(v -> {
                    Toast.makeText(MainActivity3.this, "VIEW TASK Clicked", Toast.LENGTH_SHORT).show();
                    // Navigate to View Task activity jika ada
                    // Intent intent = new Intent(MainActivity3.this, MainActivity5.class);
                    // startActivity(intent);
                });
            }

            // Setup NOTES button - NAVIGASI KE MainActivity4
            LinearLayout btnNotes = findViewById(R.id.btnNotes);
            if (btnNotes != null) {
                btnNotes.setOnClickListener(v -> {
                    Toast.makeText(MainActivity3.this, "NOTES Clicked", Toast.LENGTH_SHORT).show();
                    // Navigate to Notes activity (MainActivity4)
                    Intent intent = new Intent(MainActivity3.this, MainActivity4.class);
                    startActivity(intent);
                });
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error setting up buttons: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupFooterNavigation() {
        try {
            // Setup Footer Home
            ImageView footerHome = findViewById(R.id.footer_home);
            if (footerHome != null) {
                footerHome.setOnClickListener(v -> {
                    Toast.makeText(MainActivity3.this, "Home Clicked", Toast.LENGTH_SHORT).show();
                    // Karena sudah di MainActivity3 (Home), tidak perlu navigasi
                });
            }

            // Setup Footer Add
            ImageView footerAdd = findViewById(R.id.footer_add);
            if (footerAdd != null) {
                footerAdd.setOnClickListener(v -> {
                    Toast.makeText(MainActivity3.this, "Add Clicked", Toast.LENGTH_SHORT).show();
                    // Navigate to Add Task activity jika ada
                    // Intent intent = new Intent(MainActivity3.this, MainActivity4.class);
                    // startActivity(intent);
                });
            }

            // Setup Footer Notes - NAVIGASI KE MainActivity4
            ImageView footerNotes = findViewById(R.id.footer_notes);
            if (footerNotes != null) {
                footerNotes.setOnClickListener(v -> {
                    Toast.makeText(MainActivity3.this, "Notes Clicked", Toast.LENGTH_SHORT).show();
                    // Navigate to Notes activity (MainActivity4)
                    Intent intent = new Intent(MainActivity3.this, MainActivity4.class);
                    startActivity(intent);
                });
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error setting up footer: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}