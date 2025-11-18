package com.example.snake;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private SnakeView snakeView;
    private ImageButton pauseButton;
    private LinearLayout pauseMenu;
    private Button resumeButton;
    private Button homeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        snakeView = findViewById(R.id.snakeView);
        pauseButton = findViewById(R.id.pauseButton);
        pauseMenu = findViewById(R.id.pauseMenu);
        resumeButton = findViewById(R.id.resumeButton);
        homeButton = findViewById(R.id.homeButton);

        pauseButton.setOnClickListener(v -> {
            snakeView.pause();
            pauseMenu.setVisibility(View.VISIBLE);
        });

        resumeButton.setOnClickListener(v -> {
            snakeView.resume();
            pauseMenu.setVisibility(View.GONE);
        });

        homeButton.setOnClickListener(v -> {
            finish(); // Go back to MainActivity
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        snakeView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        snakeView.resume();
    }
}
