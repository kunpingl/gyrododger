package com.example.gyrododger;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class EndActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        Button playAgain = findViewById(R.id.playAgain);
        playAgain.setOnClickListener(unused -> {
            finish();
        });
    }
}
