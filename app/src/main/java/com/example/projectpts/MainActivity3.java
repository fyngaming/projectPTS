package com.example.projectpts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
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
            View rootView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);

            if (rootView instanceof LinearLayout) {
                LinearLayout mainLayout = (LinearLayout) rootView;

                if (mainLayout.getChildCount() > 1) {
                    View buttonContainerView = mainLayout.getChildAt(1);

                    if (buttonContainerView instanceof LinearLayout) {
                        LinearLayout buttonContainer = (LinearLayout) buttonContainerView;

                        // Setup ADD TASK (index 0)
                        if (buttonContainer.getChildCount() > 0) {
                            setupClickableLayout((LinearLayout) buttonContainer.getChildAt(0),
                                    "ADD TASK Clicked");
                        }

                        // Setup VIEW TASK (index 1)
                        if (buttonContainer.getChildCount() > 1) {
                            setupClickableLayout((LinearLayout) buttonContainer.getChildAt(1),
                                    "VIEW TASK Clicked");
                        }

                        // Setup NOTES (index 2)
                        if (buttonContainer.getChildCount() > 2) {
                            setupClickableLayout((LinearLayout) buttonContainer.getChildAt(2),
                                    "NOTES Clicked");
                        }
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error setting up interface", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupClickableLayout(LinearLayout layout, String message) {
        layout.setClickable(true);
        layout.setFocusable(true);

        layout.setOnClickListener(v ->
                Toast.makeText(MainActivity3.this, message, Toast.LENGTH_SHORT).show()
        );
    }
}