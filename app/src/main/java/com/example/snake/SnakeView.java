package com.example.snake;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class SnakeView extends View {

    // Direcciones posibles de movimiento
    private enum Direccion {
        ARRIBA, ABAJO, IZQUIERDA, DERECHA
    }

    private Direccion direccion = Direccion.DERECHA;
    private float inicioX, inicioY;
    private final List<Point> serpiente = new ArrayList<>();
    private Point comida;
    private boolean jugando = false;
    private boolean juegoTerminado = false;
    private int puntuacion = 0;

    private final int tamanoBloque = 60; // Tamaño de cada celda en píxeles
    private final Handler manejador = new Handler();
    private final long retrasoActualizacion = 200; // Velocidad de actualización del juego (ms)

    private final Paint pinturaSerpiente = new Paint();
    private final Paint pinturaComida = new Paint();
    private final Paint pinturaCuadricula = new Paint();

    private MediaPlayer sonidoComer;

    private static final int UMBRAL_DESLIZAMIENTO = 100; // Distancia mínima para detectar un deslizamiento


    public SnakeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        
        // Configuración del color y estilo de la serpiente (verde neón)
        pinturaSerpiente.setColor(ContextCompat.getColor(context, R.color.snakeColor));
        pinturaSerpiente.setAntiAlias(true); // Bordes suaves

        // Configuración del color y estilo de la comida (rojo rosado)
        pinturaComida.setColor(ContextCompat.getColor(context, R.color.foodColor));
        pinturaComida.setAntiAlias(true); // Bordes suaves

        // Configuración de la cuadrícula de fondo
        pinturaCuadricula.setColor(ContextCompat.getColor(context, R.color.boardColor));
        pinturaCuadricula.setStrokeWidth(2);
        
        // Inicializar el reproductor de sonido con el archivo de audio "ehhsound.mp3"
        sonidoComer = MediaPlayer.create(context, R.raw.ehhsound);
    }

    /**
     * parametros con los que inicia
     * (datos obtenidos de la pantallas)
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        inicializarJuego();
    }

    /**
     * empieza spawneando la serpiente.
     */
    private void inicializarJuego() {
        serpiente.clear();
        serpiente.add(new Point(5, 5)); // Posición inicial
        generarComida();
        jugando = true;
        juegoTerminado = false;
        puntuacion = 0;
        direccion = Direccion.DERECHA;
        
        // Reiniciar el bucle de actualización
        manejador.removeCallbacks(bucleJuego);
        manejador.postDelayed(bucleJuego, retrasoActualizacion);
    }

    /**
     * Genera una nueva posición para la comida en una ubicación aleatoria.
     */
    private void generarComida() {
        Random random = new Random();
        int anchoEnBloques = getWidth() / tamanoBloque;
        int altoEnBloques = getHeight() / tamanoBloque;
        
        if (anchoEnBloques > 0 && altoEnBloques > 0) {
            Point nuevaPosicionComida;
            boolean comidaSobreSerpiente;
            do {
                nuevaPosicionComida = new Point(random.nextInt(anchoEnBloques), random.nextInt(altoEnBloques));
                comidaSobreSerpiente = false;
                // Verificar colisión con el cuerpo de la serpiente
                for (Point p : serpiente) {
                    if (p.equals(nuevaPosicionComida)) {
                        comidaSobreSerpiente = true;
                        break;
                    }
                }
            } while (comidaSobreSerpiente);
            comida = nuevaPosicionComida;
        }
    }

    /**
     * Actualiza la lógica y redibuja la pantalla.
     */
    private final Runnable bucleJuego = new Runnable() {
        @Override
        public void run() {
            if (jugando) {
                actualizar();       // Actualizar lógica (movimiento, colisiones)
                invalidate();   // Forzar redibujado (llama a onDraw)
                manejador.postDelayed(this, retrasoActualizacion); // Programar siguiente ejecución
            }
        }
    };

    /**
     * Actualiza la lógica del juego: mueve la serpiente, comprueba colisiones y si come comida.
     */
    private void actualizar() {
        if (comida == null) {
            generarComida();
            if (comida == null) return;
        }

        // Calcular la nueva posición de la cabeza basada en la dirección actual
        Point cabeza = new Point(serpiente.get(0));
        switch (direccion) {
            case ARRIBA: cabeza.y--; break;
            case ABAJO: cabeza.y++; break;
            case IZQUIERDA: cabeza.x--; break;
            case DERECHA: cabeza.x++; break;
        }

        // Verificar colisión con los bordes de la pantalla
        int anchoEnBloques = getWidth() / tamanoBloque;
        int altoEnBloques = getHeight() / tamanoBloque;
        if (cabeza.x < 0 || cabeza.x >= anchoEnBloques || cabeza.y < 0 || cabeza.y >= altoEnBloques) {
            finDelJuego();
            return;
        }

        // Verificar colisión con el propio cuerpo
        for (int i = 1; i < serpiente.size(); i++) {
            if (cabeza.equals(serpiente.get(i))) {
                finDelJuego();
                return;
            }
        }

        // Mover la serpiente añadiendo la nueva cabeza
        serpiente.add(0, cabeza);

        // Verificar si ha comido
        if (cabeza.equals(comida)) {
            puntuacion++;
            reproducirSonidoComer(); // Reproducir sonido
            generarComida();    // Generar nueva comida
        } else {
            // Si no come, eliminamos la cola para mantener el tamaño (simulando movimiento)
            serpiente.remove(serpiente.size() - 1);
        }
    }

    /**
     * Reproduce el sonido de comer.
     */
    private void reproducirSonidoComer() {
        if (sonidoComer != null) {
            if (sonidoComer.isPlaying()) {
                sonidoComer.seekTo(0); // Reiniciar si ya se está reproduciendo
            }
            sonidoComer.start();
        }
    }

    /**
     * Maneja el fin del juego. Detiene el bucle, guarda la puntuación y cierra la actividad.
     */
    private void finDelJuego() {
        jugando = false;
        juegoTerminado = true;
        manejador.removeCallbacks(bucleJuego);
        guardarPuntuacionMaxima();
        ((Activity) getContext()).finish(); // Volver al menú principal
    }

    /**
     * Guarda la puntuación máxima en SharedPreferences si la puntuación actual es mayor.
     */
    private void guardarPuntuacionMaxima() {
        SharedPreferences prefs = getContext().getSharedPreferences("SnakeGame", Context.MODE_PRIVATE);
        int puntuacionMaxima = prefs.getInt("highScore", 0);
        if (puntuacion > puntuacionMaxima) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("highScore", puntuacion);
            editor.apply();
        }
    }

    /**
     * Dibuja los elementos del juego en el Canvas: cuadrícula, comida y serpiente.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Dibujar cuadrícula de fondo
        int ancho = getWidth();
        int alto = getHeight();
        
        for (int i = 0; i < ancho; i += tamanoBloque) {
            canvas.drawLine(i, 0, i, alto, pinturaCuadricula);
        }
        for (int j = 0; j < alto; j += tamanoBloque) {
            canvas.drawLine(0, j, ancho, j, pinturaCuadricula);
        }

        if (comida == null) return;

        // Dibujar comida (Círculo)
        float radio = tamanoBloque / 2f;
        float relleno = 8f; 
        canvas.drawCircle(
                comida.x * tamanoBloque + radio,
                comida.y * tamanoBloque + radio,
                radio - relleno,
                pinturaComida);

        // Dibujar serpiente (Rectángulos redondeados)
        for (Point p : serpiente) {
             float izquierda = p.x * tamanoBloque + relleno;
             float arriba = p.y * tamanoBloque + relleno;
             float derecha = (p.x + 1) * tamanoBloque - relleno;
             float abajo = (p.y + 1) * tamanoBloque - relleno;
             
             canvas.drawRoundRect(izquierda, arriba, derecha, abajo, 15f, 15f, pinturaSerpiente);
        }
    }

    /**
     * Maneja los eventos táctiles para controlar la dirección de la serpiente mediante deslizamientos (swipes).
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!jugando || juegoTerminado) return true;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                inicioX = event.getX();
                inicioY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float finX = event.getX();
                float finY = event.getY();
                float deltaX = finX - inicioX;
                float deltaY = finY - inicioY;

                // Detectar si el movimiento supera el umbral mínimo para considerarse un deslizamiento
                if (Math.abs(deltaX) > UMBRAL_DESLIZAMIENTO || Math.abs(deltaY) > UMBRAL_DESLIZAMIENTO) {
                    if (Math.abs(deltaX) > Math.abs(deltaY)) {
                        // Deslizamiento horizontal
                        if (deltaX > 0 && direccion != Direccion.IZQUIERDA) direccion = Direccion.DERECHA;
                        else if (deltaX < 0 && direccion != Direccion.DERECHA) direccion = Direccion.IZQUIERDA;
                    } else {
                        // Deslizamiento vertical
                        if (deltaY > 0 && direccion != Direccion.ARRIBA) direccion = Direccion.ABAJO;
                        else if (deltaY < 0 && direccion != Direccion.ABAJO) direccion = Direccion.ARRIBA;
                    }
                }
                break;
        }
        return true;
    }
    
    /**
     * Libera los recursos (MediaPlayer) cuando la vista se desconecta de la ventana.
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (sonidoComer != null) {
            sonidoComer.release();
            sonidoComer = null;
        }
    }
}
