package com.example.snake;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Actividad que aloja el juego de la serpiente.
 * Muestra el layout activity_game.xml que contiene el tablero de juego (SnakeView).
 * La lógica del juego se maneja dentro de la clase SnakeView.
 */
public class GameActivity extends AppCompatActivity {

    /**
     * Se llama al crear la actividad. Establece la vista del juego.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
    }
}
