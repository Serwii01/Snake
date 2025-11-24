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

/**
 * Clase principal del juego que maneja la lógica, el renderizado y los controles de la serpiente.
 * Hereda de View para dibujar gráficos personalizados en la pantalla.
 */
public class SnakeView extends View {

    // Direcciones posibles de movimiento
    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private Direction dir = Direction.RIGHT;
    private float startX, startY;
    private final List<Point> snake = new ArrayList<>();
    private Point food;
    private boolean isPlaying = false;
    private boolean isGameOver = false;
    private int score = 0;

    private final int blockSize = 60; // Tamaño de cada celda en píxeles
    private final Handler handler = new Handler();
    private final long updateDelay = 200; // Velocidad de actualización del juego (ms)

    private final Paint snakePaint = new Paint();
    private final Paint foodPaint = new Paint();
    private final Paint gridPaint = new Paint();

    private MediaPlayer eatSound;

    private static final int SWIPE_THRESHOLD = 100; // Distancia mínima para detectar un deslizamiento

    /**
     * Constructor de la vista. Inicializa los colores, pinceles y el sonido.
     */
    public SnakeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        
        // Configuración del color y estilo de la serpiente (verde neón)
        snakePaint.setColor(ContextCompat.getColor(context, R.color.snakeColor));
        snakePaint.setAntiAlias(true); // Bordes suaves

        // Configuración del color y estilo de la comida (rojo rosado)
        foodPaint.setColor(ContextCompat.getColor(context, R.color.foodColor));
        foodPaint.setAntiAlias(true); // Bordes suaves

        // Configuración de la cuadrícula de fondo
        gridPaint.setColor(ContextCompat.getColor(context, R.color.boardColor));
        gridPaint.setStrokeWidth(2);
        
        // Inicializar el reproductor de sonido con el archivo de audio "ehhsound.mp3"
        eatSound = MediaPlayer.create(context, R.raw.ehhsound);
    }

    /**
     * Se llama cuando cambia el tamaño de la vista. 
     * Se usa para iniciar el juego una vez que conocemos las dimensiones de la pantalla.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initGame();
    }

    /**
     * Inicializa o reinicia el estado del juego.
     * Coloca la serpiente en la posición inicial, genera comida y arranca el bucle de juego.
     */
    private void initGame() {
        snake.clear();
        snake.add(new Point(5, 5)); // Posición inicial
        spawnFood();
        isPlaying = true;
        isGameOver = false;
        score = 0;
        dir = Direction.RIGHT;
        
        // Reiniciar el bucle de actualización
        handler.removeCallbacks(gameLoop);
        handler.postDelayed(gameLoop, updateDelay);
    }

    /**
     * Genera una nueva posición para la comida en una ubicación aleatoria.
     * Se asegura de que la comida no aparezca sobre el cuerpo de la serpiente.
     */
    private void spawnFood() {
        Random random = new Random();
        int widthInBlocks = getWidth() / blockSize;
        int heightInBlocks = getHeight() / blockSize;
        
        if (widthInBlocks > 0 && heightInBlocks > 0) {
            Point newFoodPosition;
            boolean foodOnSnake;
            do {
                newFoodPosition = new Point(random.nextInt(widthInBlocks), random.nextInt(heightInBlocks));
                foodOnSnake = false;
                // Verificar colisión con el cuerpo de la serpiente
                for (Point p : snake) {
                    if (p.equals(newFoodPosition)) {
                        foodOnSnake = true;
                        break;
                    }
                }
            } while (foodOnSnake);
            food = newFoodPosition;
        }
    }

    /**
     * Bucle principal del juego. Se ejecuta repetidamente cada 'updateDelay' milisegundos.
     * Actualiza la lógica y redibuja la pantalla.
     */
    private final Runnable gameLoop = new Runnable() {
        @Override
        public void run() {
            if (isPlaying) {
                update();       // Actualizar lógica (movimiento, colisiones)
                invalidate();   // Forzar redibujado (llama a onDraw)
                handler.postDelayed(this, updateDelay); // Programar siguiente ejecución
            }
        }
    };

    /**
     * Actualiza la lógica del juego: mueve la serpiente, comprueba colisiones y si come comida.
     */
    private void update() {
        if (food == null) {
            spawnFood();
            if (food == null) return;
        }

        // Calcular la nueva posición de la cabeza basada en la dirección actual
        Point head = new Point(snake.get(0));
        switch (dir) {
            case UP: head.y--; break;
            case DOWN: head.y++; break;
            case LEFT: head.x--; break;
            case RIGHT: head.x++; break;
        }

        // Verificar colisión con los bordes de la pantalla
        int widthInBlocks = getWidth() / blockSize;
        int heightInBlocks = getHeight() / blockSize;
        if (head.x < 0 || head.x >= widthInBlocks || head.y < 0 || head.y >= heightInBlocks) {
            gameOver();
            return;
        }

        // Verificar colisión con el propio cuerpo
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                gameOver();
                return;
            }
        }

        // Mover la serpiente añadiendo la nueva cabeza
        snake.add(0, head);

        // Verificar si ha comido
        if (head.equals(food)) {
            score++;
            playEatSound(); // Reproducir sonido
            spawnFood();    // Generar nueva comida
        } else {
            // Si no come, eliminamos la cola para mantener el tamaño (simulando movimiento)
            snake.remove(snake.size() - 1);
        }
    }

    /**
     * Reproduce el sonido de comer.
     */
    private void playEatSound() {
        if (eatSound != null) {
            if (eatSound.isPlaying()) {
                eatSound.seekTo(0); // Reiniciar si ya se está reproduciendo
            }
            eatSound.start();
        }
    }

    /**
     * Maneja el fin del juego. Detiene el bucle, guarda la puntuación y cierra la actividad.
     */
    private void gameOver() {
        isPlaying = false;
        isGameOver = true;
        handler.removeCallbacks(gameLoop);
        saveHighScore();
        ((Activity) getContext()).finish(); // Volver al menú principal
    }

    /**
     * Guarda la puntuación máxima en SharedPreferences si la puntuación actual es mayor.
     */
    private void saveHighScore() {
        SharedPreferences prefs = getContext().getSharedPreferences("SnakeGame", Context.MODE_PRIVATE);
        int highScore = prefs.getInt("highScore", 0);
        if (score > highScore) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("highScore", score);
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
        int width = getWidth();
        int height = getHeight();
        
        for (int i = 0; i < width; i += blockSize) {
            canvas.drawLine(i, 0, i, height, gridPaint);
        }
        for (int j = 0; j < height; j += blockSize) {
            canvas.drawLine(0, j, width, j, gridPaint);
        }

        if (food == null) return;

        // Dibujar comida (Círculo)
        float radius = blockSize / 2f;
        float padding = 8f; 
        canvas.drawCircle(
                food.x * blockSize + radius,
                food.y * blockSize + radius,
                radius - padding,
                foodPaint);

        // Dibujar serpiente (Rectángulos redondeados)
        for (Point p : snake) {
             float left = p.x * blockSize + padding;
             float top = p.y * blockSize + padding;
             float right = (p.x + 1) * blockSize - padding;
             float bottom = (p.y + 1) * blockSize - padding;
             
             canvas.drawRoundRect(left, top, right, bottom, 15f, 15f, snakePaint);
        }
    }

    /**
     * Maneja los eventos táctiles para controlar la dirección de la serpiente mediante deslizamientos (swipes).
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isPlaying || isGameOver) return true;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float endX = event.getX();
                float endY = event.getY();
                float deltaX = endX - startX;
                float deltaY = endY - startY;

                // Detectar si el movimiento supera el umbral mínimo para considerarse un deslizamiento
                if (Math.abs(deltaX) > SWIPE_THRESHOLD || Math.abs(deltaY) > SWIPE_THRESHOLD) {
                    if (Math.abs(deltaX) > Math.abs(deltaY)) {
                        // Deslizamiento horizontal
                        if (deltaX > 0 && dir != Direction.LEFT) dir = Direction.RIGHT;
                        else if (deltaX < 0 && dir != Direction.RIGHT) dir = Direction.LEFT;
                    } else {
                        // Deslizamiento vertical
                        if (deltaY > 0 && dir != Direction.UP) dir = Direction.DOWN;
                        else if (deltaY < 0 && dir != Direction.DOWN) dir = Direction.UP;
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
        if (eatSound != null) {
            eatSound.release();
            eatSound = null;
        }
    }
}
