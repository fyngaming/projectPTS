        package com.example.projectpts;

        import android.content.Intent;
        import android.os.Bundle;
        import android.widget.Button;
        import android.widget.Toast;
        import androidx.activity.OnBackPressedCallback;
        import androidx.appcompat.app.AppCompatActivity;

        public class MainActivity2 extends AppCompatActivity {

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main2);

                 Button btnGetStarted = findViewById(R.id.btnGetStarted);

                btnGetStarted.setOnClickListener(v -> {
                    Toast.makeText(MainActivity2.this, "Welcome! Let's get started!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MainActivity2.this, MainActivity3.class);
                    startActivity(intent);
                });

                getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }