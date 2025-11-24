package com.example.snake;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Actividad principal de la aplicación. Muestra el menú de inicio con el título,
 * la puntuación máxima y el botón para comenzar a jugar.
 */
public class MainActivity extends AppCompatActivity {

    // Vista de texto para mostrar la puntuación máxima alcanzada
    private TextView scoreValueText;

    /**
     * Se llama al crear la actividad. Inicializa la interfaz de usuario.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtener referencia al TextView del diseño para poder actualizarlo después
        scoreValueText = findViewById(R.id.scoreValueText);
    }

    /**
     * Método vinculado al botón "Jugar" en el archivo activity_main.xml (android:onClick="startGame").
     * Inicia la GameActivity para comenzar el juego.
     *
     * @param view La vista que disparó el evento (el botón).
     */
    public void startGame(View view) {
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        startActivity(intent);
    }

    /**
     * Se llama cuando la actividad vuelve a primer plano.
     * Actualizamos la puntuación máxima aquí para asegurar que se muestre el valor más reciente
     * si el usuario acaba de volver de jugar una partida.
     */
    @Override
    protected void onResume() {
        super.onResume();
        
        // Leer la puntuación máxima guardada en las preferencias compartidas
        SharedPreferences prefs = getSharedPreferences("SnakeGame", MODE_PRIVATE);
        int highScore = prefs.getInt("highScore", 0);
        
        // Mostrar la puntuación en el TextView
        if (scoreValueText != null) {
            scoreValueText.setText(String.valueOf(highScore));
        }
    }
}
