package com.nissa.loginsqlite;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    private EditText edtEmail;
    private EditText edtPassword;
    private Button btnLogin;
    private Button btnToRegister;
    private DatabaseHelper db;

    // Konstanta untuk mengirim data ke MainActivity
    public static final String EXTRA_USER_NAME = "user_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inisialisasi view dan database
        initViews();
        db = new DatabaseHelper(this);

        // Atur event listener
        setupListeners();
    }

    private void initViews() {
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnToRegister = findViewById(R.id.btnToRegister);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());
        btnToRegister.setOnClickListener(v -> navigateToRegister());
    }

    private void attemptLogin() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString();

        // Reset error
        edtEmail.setError(null);
        edtPassword.setError(null);

        boolean cancel = false;

        // Validasi email
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Email tidak boleh kosong");
            cancel = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Format email tidak valid");
            cancel = true;
        }

        // Validasi password
        if (TextUtils.isEmpty(password)) {
            edtPassword.setError("Password tidak boleh kosong");
            if (!cancel) edtPassword.requestFocus();
            cancel = true;
        } else if (password.length() < 6) {
            edtPassword.setError("Password minimal 6 karakter");
            if (!cancel) edtPassword.requestFocus();
            cancel = true;
        }

        if (cancel) {
            // Jangan lanjut jika ada error
            return;
        }

        // Cek user di database
        if (db.checkUser(email, password)) {
            String name = db.getNameByEmail(email);

            // Gunakan email jika nama tidak ditemukan
            if (name == null || name.isEmpty()) {
                name = email;
            }

            // Pindah ke MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(EXTRA_USER_NAME, name);
            startActivity(intent);
            finish();
        } else {
            // Login gagal
            Toast.makeText(this, "Email atau password salah", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToRegister() {
        startActivity(new Intent(this, Register.class));
        finish();
    }
}
