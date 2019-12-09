package com.example.gyrododger;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    private int screenWidth;
    private int screenHeight;

    private ImageView player;
    private int playerX;
    private int playerY;

    private float playerL;
    private float playerT;

    private ImageView redBall;
    private float redBallX;
    private float redBallY;

    private Timer timer = new Timer();
    private Handler handler = new Handler();
    private Random random = new Random();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        MediaPlayer ring = MediaPlayer.create(GameActivity.this, R.raw.music);
        ring.start();
        ring.setLooping(true);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        player = findViewById(R.id.player);
        player.setOnTouchListener(movingEventListener);

        redBall = findViewById(R.id.redBall);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        redBallLogic();
                    }
                });
            }
        }, 0, 20);
    }

    private boolean CollisionCheck(float x, float y) {
        if (playerL <= x && x <= playerL + player.getWidth() &&
                playerT <= y && y <= playerT + player.getHeight()) {
            return true;
        }
        return false;
    }

    private void redBallLogic() {
        redBallY += random.nextInt(100);
        float redCenterX = redBallX + redBall.getWidth() / 2;
        float redCenterY = redBallY + redBall.getHeight() / 2;

        if (CollisionCheck(redCenterX, redCenterY)) {
            redBallY = screenHeight + 50;
        }

        if (redBallY > screenHeight) {
            redBallY = -100;
            redBallX = (float) Math.floor(Math.random() * (screenWidth - redBall.getWidth()));
        }
        redBall.setX(redBallX);
        redBall.setY(redBallY);
    }

    private void setBound(View view, int leftBound, int rightBound, int topBound, int bottomBound) {
        // The player can not pass over the bound of screen
        if (leftBound < 0) {
            leftBound = 0;
            rightBound = leftBound + view.getWidth();
        }

        if (rightBound > screenWidth) {
            rightBound = screenWidth;
            leftBound = rightBound - view.getWidth();
        }

        if (topBound < 0) {
            topBound = 0;
            bottomBound = topBound + view.getHeight();
        }

        if (bottomBound > screenHeight) {
            bottomBound = screenHeight;
            topBound = bottomBound - view.getHeight();
        }

        view.layout(leftBound, topBound, rightBound, bottomBound);
    }


    private OnTouchListener movingEventListener = new OnTouchListener() {
        int xDelta;
        int yDelta;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    playerX = (int) event.getRawX();
                    playerY = (int) event.getRawY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    xDelta = (int) event.getRawX() - playerX;
                    yDelta = (int) event.getRawY() - playerY;

                    int leftBound = view.getLeft() + xDelta;
                    int topBound = view.getTop() + yDelta;
                    int rightBound = view.getRight() + xDelta;
                    int bottomBound = view.getBottom() + yDelta;

                    setBound(view, leftBound, rightBound, topBound, bottomBound);

                    playerX = (int) event.getRawX();
                    playerY = (int) event.getRawY();

                    playerL = player.getLeft();
                    playerT = player.getTop();
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return true;
        }
    };
}
