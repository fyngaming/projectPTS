package com.example.projectpts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity8 extends AppCompatActivity {

    private LinearLayout notesContainer;
    private FloatingActionButton fabAddNote;
    private ImageButton backButton;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    private static final String PREFS_NAME = "NotesPrefs";
    private static final String NOTES_KEY = "notes";

    // Warna untuk note cards
    private final String[] noteColors = {
            "#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4", "#FFEAA7",
            "#DDA0DD", "#98D8C8", "#F7DC6F", "#BB8FCE", "#85C1E9"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main8);

        // Initialize SharedPreferences dan Gson
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        gson = new Gson();

        initViews();
        setupBackButton();
        setupFAB();
        loadAndDisplayNotes();
    }

    private void initViews() {
        notesContainer = findViewById(R.id.notesContainer);
        fabAddNote = findViewById(R.id.fabAddNote);
        backButton = findViewById(R.id.backButton);
    }

    private void setupBackButton() {
        backButton.setOnClickListener(v -> {
            kembaliKeMainActivity3();
        });
    }

    private void setupFAB() {
        fabAddNote.setOnClickListener(v -> {
            // Pindah ke MainActivity4 untuk membuat note baru
            Intent intent = new Intent(MainActivity8.this, MainActivity4.class);
            startActivity(intent);
        });
    }

    private void kembaliKeMainActivity3() {
        Intent intent = new Intent(MainActivity8.this, MainActivity3.class);
        startActivity(intent);
        finish();
    }

    private void loadAndDisplayNotes() {
        List<Note> notes = loadNotesFromStorage();

        if (notes.isEmpty()) {
            showEmptyState();
        } else {
            displayNotes(notes);
        }
    }

    private void displayNotes(List<Note> notes) {
        notesContainer.removeAllViews();

        for (int i = 0; i < notes.size(); i++) {
            Note note = notes.get(i);
            addNoteToView(note, i);
        }

        Toast.makeText(this, "Menampilkan " + notes.size() + " catatan", Toast.LENGTH_SHORT).show();
    }

    private void addNoteToView(Note note, int position) {
        View noteItem = createNoteItemView(note, position);
        notesContainer.addView(noteItem);
    }

    private View createNoteItemView(Note note, int position) {
        // Create CardView sebagai container
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, dpToPx(16));
        cardView.setLayoutParams(cardParams);
        cardView.setCardElevation(dpToPx(4));
        cardView.setRadius(dpToPx(12));
        cardView.setCardBackgroundColor(Color.WHITE);
        cardView.setContentPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

        // Create inner LinearLayout
        LinearLayout noteLayout = new LinearLayout(this);
        noteLayout.setOrientation(LinearLayout.VERTICAL);
        noteLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // Header section dengan title dan color indicator
        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // Color indicator
        View colorIndicator = new View(this);
        LinearLayout.LayoutParams colorParams = new LinearLayout.LayoutParams(
                dpToPx(4),
                dpToPx(24)
        );
        colorParams.setMargins(0, 0, dpToPx(12), 0);
        colorIndicator.setLayoutParams(colorParams);
        colorIndicator.setBackgroundColor(Color.parseColor(noteColors[position % noteColors.length]));
        colorIndicator.setBackground(getResources().getDrawable(R.drawable.color_indicator_gradient));

        // Note title
        TextView tvNoteTitle = new TextView(this);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        );
        tvNoteTitle.setLayoutParams(titleParams);
        tvNoteTitle.setText(note.getTitle());
        tvNoteTitle.setTextColor(Color.parseColor("#2C3E50"));
        tvNoteTitle.setTextSize(18);
        tvNoteTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        tvNoteTitle.setMaxLines(1);
        tvNoteTitle.setEllipsize(android.text.TextUtils.TruncateAt.END);

        // Add views to header
        headerLayout.addView(colorIndicator);
        headerLayout.addView(tvNoteTitle);

        // Note content
        TextView tvNoteContent = new TextView(this);
        LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        contentParams.setMargins(0, dpToPx(8), 0, 0);
        tvNoteContent.setLayoutParams(contentParams);
        tvNoteContent.setText(note.getContent());
        tvNoteContent.setTextColor(Color.parseColor("#666666"));
        tvNoteContent.setTextSize(14);
        tvNoteContent.setMaxLines(3);
        tvNoteContent.setEllipsize(android.text.TextUtils.TruncateAt.END);
        tvNoteContent.setLineSpacing(dpToPx(2), 1);

        // Note date
        TextView tvNoteDate = new TextView(this);
        LinearLayout.LayoutParams dateParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        dateParams.setMargins(0, dpToPx(8), 0, 0);
        tvNoteDate.setLayoutParams(dateParams);

        String noteDate = "Tanggal tidak tersedia";
        try {
            if (note.getDate() != null && !note.getDate().isEmpty()) {
                noteDate = note.getDate();
            } else {
                noteDate = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(new Date());
            }
        } catch (Exception e) {
            noteDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
        }

        tvNoteDate.setText(noteDate);
        tvNoteDate.setTextColor(Color.parseColor("#999999"));
        tvNoteDate.setTextSize(12);

        // Add all views to note layout
        noteLayout.addView(headerLayout);
        noteLayout.addView(tvNoteContent);
        noteLayout.addView(tvNoteDate);

        // Add note layout to card view
        cardView.addView(noteLayout);

        // Set click listeners
        cardView.setOnClickListener(v -> openNoteDetail(note));
        cardView.setOnLongClickListener(v -> {
            deleteNote(note);
            return true;
        });

        return cardView;
    }

    private void openNoteDetail(Note note) {
        Intent intent = new Intent(MainActivity8.this, MainActivity9.class);
        intent.putExtra("NOTE_TITLE", note.getTitle());
        intent.putExtra("NOTE_CONTENT", note.getContent());

        String noteDate = "Tanggal tidak tersedia";
        try {
            if (note.getDate() != null && !note.getDate().isEmpty()) {
                noteDate = note.getDate();
            }
        } catch (Exception e) {
            noteDate = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date());
        }

        intent.putExtra("NOTE_DATE", noteDate);
        startActivity(intent);
    }

    private void deleteNote(Note note) {
        List<Note> notes = loadNotesFromStorage();
        notes.removeIf(n -> n.getId().equals(note.getId()));
        saveNotesToStorage(notes);
        loadAndDisplayNotes();
        Toast.makeText(this, "Catatan dihapus", Toast.LENGTH_SHORT).show();
    }

    private void showEmptyState() {
        notesContainer.removeAllViews();

        // Tampilkan pesan kosong dengan desain yang lebih baik
        CardView emptyCard = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(200)
        );
        cardParams.setMargins(0, dpToPx(50), 0, 0);
        emptyCard.setLayoutParams(cardParams);
        emptyCard.setCardElevation(dpToPx(2));
        emptyCard.setRadius(dpToPx(12));
        emptyCard.setCardBackgroundColor(Color.parseColor("#30FFFFFF"));

        LinearLayout emptyLayout = new LinearLayout(this);
        emptyLayout.setOrientation(LinearLayout.VERTICAL);
        emptyLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        emptyLayout.setGravity(android.view.Gravity.CENTER);
        emptyLayout.setPadding(dpToPx(24), dpToPx(24), dpToPx(24), dpToPx(24));

        TextView emptyText = new TextView(this);
        emptyText.setText("Belum ada catatan\n\nKlik tombol + untuk menambah\ncatatan pertama Anda!");
        emptyText.setTextColor(Color.WHITE);
        emptyText.setTextSize(16);
        emptyText.setGravity(android.view.Gravity.CENTER);
        emptyText.setLineSpacing(dpToPx(4), 1);

        emptyLayout.addView(emptyText);
        emptyCard.addView(emptyLayout);
        notesContainer.addView(emptyCard);
    }

    private List<Note> loadNotesFromStorage() {
        String notesJson = sharedPreferences.getString(NOTES_KEY, "");
        if (notesJson.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            Type type = new TypeToken<List<Note>>(){}.getType();
            List<Note> noteList = gson.fromJson(notesJson, type);
            return noteList != null ? noteList : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void saveNotesToStorage(List<Note> notes) {
        String notesJson = gson.toJson(notes);
        sharedPreferences.edit().putString(NOTES_KEY, notesJson).apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data setiap kali kembali ke activity ini
        loadAndDisplayNotes();
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}