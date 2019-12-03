package com.example.gyrododger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

public class StartActiviy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ImageButton btn_main_play = findViewById(R.id.btn_main_play);
        btn_main_play.setOnClickListener(unused -> {
            startActivityForResult(new Intent(this, GameActivity.class),1);
        });
    }
}
