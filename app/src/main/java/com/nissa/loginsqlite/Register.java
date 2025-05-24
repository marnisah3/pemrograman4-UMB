package com.nissa.loginsqlite;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils; // Import untuk TextUtils
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Register extends AppCompatActivity {

    private EditText edtName;
    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtConfirmPassword;
    private Button btnRegister;
    private Button btnToLogin;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inisialisasi View
        initViews();

        // Inisialisasi DatabaseHelper
        db = new DatabaseHelper(this);

        // Atur Listeners
        setupListeners();
    }

    private void initViews() {
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnToLogin = findViewById(R.id.btnToLogin);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> attemptRegister());
        btnToLogin.setOnClickListener(v -> navigateToLogin());
    }

    private void attemptRegister() {
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString();
        String confirmPassword = edtConfirmPassword.getText().toString();

        // Reset error state
        edtName.setError(null);
        edtEmail.setError(null);
        edtPassword.setError(null);
        edtConfirmPassword.setError(null);

        boolean cancel = false; // Flag untuk menandai apakah ada error
        edtName.requestFocus(); // Set fokus awal ke nama (jika ada error di sana)

        // Validasi input Nama
        if (TextUtils.isEmpty(name)) {
            edtName.setError("Nama wajib diisi");
            cancel = true;
        }

        // Validasi input Email
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Email tidak boleh kosong");
            if (!cancel) edtEmail.requestFocus(); // Fokus ke email jika nama tidak error
            cancel = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Format email tidak valid");
            if (!cancel) edtEmail.requestFocus();
            cancel = true;
        }

        // Validasi input Password
        if (TextUtils.isEmpty(password)) {
            edtPassword.setError("Password tidak boleh kosong");
            if (!cancel) edtPassword.requestFocus();
            cancel = true;
        } else if (password.length() < 6) {
            edtPassword.setError("Password minimal 6 karakter");
            if (!cancel) edtPassword.requestFocus();
            cancel = true;
        }

        // Validasi input Konfirmasi Password
        if (TextUtils.isEmpty(confirmPassword)) {
            edtConfirmPassword.setError("Konfirmasi password tidak boleh kosong");
            if (!cancel) edtConfirmPassword.requestFocus();
            cancel = true;
        } else if (!password.equals(confirmPassword)) {
            edtConfirmPassword.setError("Konfirmasi password tidak cocok");
            if (!cancel) edtConfirmPassword.requestFocus();
            cancel = true;
        }

        if (cancel) {
            // Jika ada error, jangan lanjutkan proses registrasi
            return;
        }

        // Lakukan registrasi user ke database
        // Periksa apakah email sudah terdaftar sebelum mencoba registrasi
        if (db.checkEmail(email)) { // Asumsi ada method checkEmail di DatabaseHelper
            Toast.makeText(this, "Email sudah digunakan", Toast.LENGTH_SHORT).show();
            edtEmail.setError("Email sudah digunakan");
            edtEmail.requestFocus();
        } else {
            boolean registered = db.registerUser(name, email, password);
            if (registered) {
                Toast.makeText(this, "Registrasi berhasil", Toast.LENGTH_SHORT).show();
                // Navigasi kembali ke LoginActivity setelah registrasi berhasil
                Intent intent = new Intent(this, Login.class);
                startActivity(intent);
                finish(); // Tutup RegisterActivity
            } else {
                // Ini seharusnya jarang terjadi jika checkEmail sudah dilakukan,
                // tapi tetap ada untuk penanganan error umum.
                Toast.makeText(this, "Registrasi gagal, coba lagi", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void navigateToLogin() {
        startActivity(new Intent(this, Login.class));
        finish(); // Tutup RegisterActivity
    }
}