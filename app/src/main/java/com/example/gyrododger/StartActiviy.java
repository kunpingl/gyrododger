package com.example.gyrododger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class StartActiviy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Button start = findViewById(R.id.start);
        start.setOnClickListener(ununsed -> {
            startActivityForResult(new Intent(this, GameActivity.class),1);
        });
    }
}
