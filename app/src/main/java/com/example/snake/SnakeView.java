package com.example.snake;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
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

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private Direction dir = Direction.RIGHT;
    private float startX, startY;
    private final List<Point> snake = new ArrayList<>();
    private Point food;
    private boolean isPlaying = false;
    private boolean isPaused = false;
    private boolean isGameOver = false;
    private int score = 0;

    private final int blockSize = 60;
    private final Handler handler = new Handler();
    private final long updateDelay = 200;

    private final Paint snakePaint = new Paint();
    private final Paint foodPaint = new Paint();

    private static final int SWIPE_THRESHOLD = 100;

    public SnakeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        snakePaint.setColor(ContextCompat.getColor(context, R.color.snakeColor));
        foodPaint.setColor(ContextCompat.getColor(context, R.color.foodColor));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initGame();
    }

    private void initGame() {
        snake.clear();
        snake.add(new Point(5, 5));
        spawnFood();
        isPlaying = true;
        isPaused = false;
        isGameOver = false;
        score = 0;
        dir = Direction.RIGHT;
        handler.removeCallbacks(gameLoop);
        handler.postDelayed(gameLoop, updateDelay);
    }

    public void pause() {
        if (!isPlaying || isGameOver) return;
        isPaused = true;
        handler.removeCallbacks(gameLoop);
    }

    public void resume() {
        if (!isPlaying || isGameOver || !isPaused) return;
        isPaused = false;
        handler.postDelayed(gameLoop, updateDelay);
    }

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

    private final Runnable gameLoop = new Runnable() {
        @Override
        public void run() {
            if (isPlaying && !isPaused) {
                update();
                invalidate();
                handler.postDelayed(this, updateDelay);
            }
        }
    };

    private void update() {
        if (food == null) {
            spawnFood();
            if (food == null) return;
        }

        Point head = new Point(snake.get(0));
        switch (dir) {
            case UP: head.y--; break;
            case DOWN: head.y++; break;
            case LEFT: head.x--; break;
            case RIGHT: head.x++; break;
        }

        int widthInBlocks = getWidth() / blockSize;
        int heightInBlocks = getHeight() / blockSize;
        if (head.x < 0 || head.x >= widthInBlocks || head.y < 0 || head.y >= heightInBlocks) {
            gameOver();
            return;
        }

        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                gameOver();
                return;
            }
        }

        snake.add(0, head);

        if (head.equals(food)) {
            score++;
            spawnFood();
        } else {
            snake.remove(snake.size() - 1);
        }
    }

    private void gameOver() {
        isPlaying = false;
        isGameOver = true;
        handler.removeCallbacks(gameLoop);
        saveHighScore();
        ((Activity) getContext()).finish();
    }

    private void saveHighScore() {
        SharedPreferences prefs = getContext().getSharedPreferences("SnakeGame", Context.MODE_PRIVATE);
        int highScore = prefs.getInt("highScore", 0);
        if (score > highScore) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("highScore", score);
            editor.apply();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (food == null) return;

        // Draw food
        canvas.drawRect(food.x * blockSize, food.y * blockSize, (food.x + 1) * blockSize, (food.y + 1) * blockSize, foodPaint);

        // Draw snake
        for (Point p : snake) {
            canvas.drawRect(p.x * blockSize, p.y * blockSize, (p.x + 1) * blockSize, (p.y + 1) * blockSize, snakePaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isPlaying || isPaused || isGameOver) return true;

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

                if (Math.abs(deltaX) > SWIPE_THRESHOLD || Math.abs(deltaY) > SWIPE_THRESHOLD) {
                    if (Math.abs(deltaX) > Math.abs(deltaY)) {
                        if (deltaX > 0 && dir != Direction.LEFT) dir = Direction.RIGHT;
                        else if (deltaX < 0 && dir != Direction.RIGHT) dir = Direction.LEFT;
                    } else {
                        if (deltaY > 0 && dir != Direction.UP) dir = Direction.DOWN;
                        else if (deltaY < 0 && dir != Direction.DOWN) dir = Direction.UP;
                    }
                }
                break;
        }
        return true;
    }
}
