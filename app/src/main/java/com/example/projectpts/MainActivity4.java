    package com.example.projectpts;

    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.os.Bundle;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ImageButton;
    import android.widget.Toast;
    import androidx.activity.OnBackPressedCallback;
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

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main4);

            initViews();
            setupSharedPreferences();
            setupButtonListeners();
            setupBackPressedHandler();
        }

        private void initViews() {
            etNoteTitle = findViewById(R.id.etNoteTitle);
            etNoteContent = findViewById(R.id.etNoteContent);
            btnAddNote = findViewById(R.id.btnAddNote);
            backButton = findViewById(R.id.backButton);
        }

        private void setupSharedPreferences() {
            sharedPreferences = getSharedPreferences("notes_prefs", MODE_PRIVATE);
            gson = new Gson();
        }

        private void setupButtonListeners() {
            backButton.setOnClickListener(v -> kembaliKeActivity3());

            btnAddNote.setOnClickListener(v -> simpanDanKembaliKeActivity3());
        }

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

            // Navigate ke MainActivity5 untuk melihat catatan
            Intent intent = new Intent(MainActivity4.this, MainActivity5.class);
            startActivity(intent);
            finish();
        }

        private void simpanCatatan(String judul, String isi) {
            // Buat objek catatan baru
            Catatan catatanBaru = new Catatan(judul, isi);

            // Load catatan yang sudah ada
            List<Catatan> catatanList = loadCatatanList();

            // Tambahkan catatan baru
            catatanList.add(catatanBaru);

            // Simpan kembali ke SharedPreferences
            saveCatatanList(catatanList);
        }

        private List<Catatan> loadCatatanList() {
            String catatanJson = sharedPreferences.getString("catatan_list", "");
            if (catatanJson.isEmpty()) {
                return new ArrayList<>();
            }

            Type type = new TypeToken<List<Catatan>>(){}.getType();
            List<Catatan> catatanList = gson.fromJson(catatanJson, type);
            return catatanList != null ? catatanList : new ArrayList<>();
        }

        private void saveCatatanList(List<Catatan> catatanList) {
            String catatanJson = gson.toJson(catatanList);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("catatan_list", catatanJson);
            editor.apply();
        }

        private void kembaliKeActivity3() {
            Intent intent = new Intent(MainActivity4.this, MainActivity3.class);
            startActivity(intent);
            finish();
        }
    }