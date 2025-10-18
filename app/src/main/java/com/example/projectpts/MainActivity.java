package com.example.projectpts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin;
    LinearLayout btnLoginGoogle;

    // Firebase
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        initViews();
        setupLoginButton();
        setupGoogleLoginButton();
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnLoginGoogle = findViewById(R.id.btnLoginGoogle);
    }

    private void setupLoginButton() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithUsernamePassword();
            }
        });
    }

    private void setupGoogleLoginButton() {
        btnLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });
    }

    private void loginWithUsernamePassword() {
        String user = etUsername.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        // Validasi input
        if (user.isEmpty()) {
            etUsername.setError("Please enter username");
            etUsername.requestFocus();
            return;
        }

        if (pass.isEmpty()) {
            etPassword.setError("Please enter password");
            etPassword.requestFocus();
            return;
        }

        // Login dengan username/password hardcoded (admin/1234)
        if (user.equals("admin") && pass.equals("1234")) {
            Toast.makeText(MainActivity.this, "Login Berhasil!", Toast.LENGTH_SHORT).show();
            navigateToMainActivity2();
        } else {
            Toast.makeText(MainActivity.this, "Username / Password salah!", Toast.LENGTH_SHORT).show();

            // Clear password field untuk keamanan
            etPassword.setText("");
            etPassword.requestFocus();
        }
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                }
            } catch (ApiException e) {
                // Google Sign In failed
                String errorMessage = "Google sign in failed";
                if (e.getStatusCode() == 10) {
                    errorMessage = "Google sign in failed: Please check SHA fingerprint configuration";
                } else {
                    errorMessage = "Google sign in failed: Error code " + e.getStatusCode();
                }
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        if (idToken == null) {
            Toast.makeText(this, "Google authentication failed", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        FirebaseUser user = mAuth.getCurrentUser();
                        String successMessage = "Login dengan Google Berhasil!";
                        if (user != null && user.getEmail() != null) {
                            successMessage = "Welcome " + user.getEmail() + "!";
                        }
                        Toast.makeText(MainActivity.this, successMessage, Toast.LENGTH_SHORT).show();
                        navigateToMainActivity2();
                    } else {
                        // Sign in failed
                        String errorMessage = "Authentication failed";
                        if (task.getException() != null) {
                            errorMessage = task.getException().getMessage();
                        }
                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void navigateToMainActivity2() {
        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
        startActivity(intent);
        finish(); // Tutup login activity agar tidak bisa kembali dengan back button
    }

    @Override
    protected void onStart() {
        super.onStart();

        // ✅ TIDAK ADA AUTO-REDIRECT - User tetap di login page
        // Clear fields untuk pengalaman user yang lebih baik
        clearLoginFields();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Clear fields setiap kali kembali ke login page
        clearLoginFields();
    }

    private void clearLoginFields() {
        etUsername.setText("");
        etPassword.setText("");
        etUsername.requestFocus(); // Set focus ke username field
    }

    // ✅ TIDAK ADA onBackPressed() - Biarkan default behavior
    // User bisa tekan back button untuk keluar app tanpa konfirmasi
}