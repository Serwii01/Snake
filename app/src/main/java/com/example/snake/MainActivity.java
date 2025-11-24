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
    private TextView textoValorRecord;

    /**
     * Se llama al crear la actividad. Inicializa la interfaz de usuario.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtener referencia al TextView del diseño para poder actualizarlo después
        textoValorRecord = findViewById(R.id.textoValorRecord);
    }

    /**
     * cambia de vista
     */
    public void iniciarJuego(View view) {
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        startActivity(intent);
    }

    /**
     * Se llama cuando la actividad vuelve a primer plano y se actualiza la puntuacion
     * se usa onresume para no tener que cerrar la app
     */
    @Override
    protected void onResume() {
        super.onResume();
        
        // Leer la puntuación máxima guardada en las preferencias compartidas
        SharedPreferences prefs = getSharedPreferences("SnakeGame", MODE_PRIVATE);
        int highScore = prefs.getInt("highScore", 0);
        
        // Mostrar la puntuación en el TextView
        if (textoValorRecord != null) {
            textoValorRecord.setText(String.valueOf(highScore));
        }
    }
}
