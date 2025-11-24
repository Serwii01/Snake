package com.example.snake;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView scoreValueText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Buscamos el TextView del número grande
        scoreValueText = findViewById(R.id.scoreValueText);
    }

    public void startGame(View view) {
        startActivity(new Intent(MainActivity.this, GameActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("SnakeGame", MODE_PRIVATE);
        int highScore = prefs.getInt("highScore", 0);
        
        // Actualizamos solo el número
        if (scoreValueText != null) {
            scoreValueText.setText(String.valueOf(highScore));
        }
    }
}
