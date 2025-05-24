package com.nissa.loginsqlite;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView tvWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvWelcome = findViewById(R.id.tvWelcome);

        String name = getIntent().getStringExtra("user_name");
        if (name != null && !name.isEmpty()) {
            tvWelcome.setText("Selamat Datang, " + name);
        } else {
            tvWelcome.setText("Selamat Datang");
        }
    }
}
