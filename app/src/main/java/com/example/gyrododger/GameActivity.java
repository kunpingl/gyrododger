package com.example.gyrododger;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
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

    private Timer timer = new Timer();
    private Handler handler = new Handler();

    private List<ImageView> enemyList;


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

        enemyList = new ArrayList<>();

        player = findViewById(R.id.player);
        player.setOnTouchListener(movingEventListener);

        ImageView redBall = findViewById(R.id.redBall);
        ImageView greenBall = findViewById(R.id.greenBall);

        enemyList.add(redBall);
        enemyList.add(greenBall);

        for (ImageView eachEnemy : enemyList) {
            respawnEnemy(eachEnemy);
        }

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (ImageView eachEnemy : enemyList) {
                            enemyMove(eachEnemy);
                        }
                    }
                });
            }
        }, 0, 20);
    }

    private boolean CollisionCheck(float l, float r, float t, float b) {
        if (r >= player.getLeft()) {
            if (t < player.getBottom() || b > player.getTop()) {
                return true;
            }
        }
        if (l <= player.getRight()) {
            if (t < player.getBottom() || b > player.getTop()) {
                return true;
            }
        }
        return false;
    }

    private boolean WallCheck(ImageView image, float x, float y) {
        if (x < - 2 * image.getWidth() || x > screenWidth + image.getWidth()
            || y < - 2 * image.getHeight() || y > screenHeight + image.getHeight()) {
            return true;
        }
        return false;
    }

    private void enemyMove(ImageView enemy) {
        float redL = enemy.getX();
        float redR = enemy.getX() + enemy.getWidth();
        float redT = enemy.getY();
        float redB = enemy.getY() + enemy.getHeight();

        float newY = enemy.getY();
        float newX = enemy.getX();

        if (enemy.getTag() == "down") {
            newY += 10;
        } else if (enemy.getTag() == "up") {
            newY -= 10;
        } else if (enemy.getTag() == "left") {
            newX -= 10;
        } else if (enemy.getTag() == "right") {
            newX += 10;
        }

        if (CollisionCheck(redL, redR, redT, redB)) {
            respawnEnemy(enemy);
        }
        if (WallCheck(enemy, redL, redT)) {
            respawnEnemy(enemy);
        }

        enemy.setY(newY);
        enemy.setX(newX);
    }

    private void respawnEnemy(ImageView image) {
        String direction;
        float x = 0;
        float y = 0;
        boolean horiOrVerti = true;
        //true is Vertical and false is Horizontal
        boolean startPoint = true;
        //true is start from 0 and false otherwise
        double side = Math.random();
        if (side > 0.49) {
            horiOrVerti = false;
        }
        side = Math.random();
        if (side > 0.49) {
            startPoint = false;
        }
        if (horiOrVerti) {
            //move vertically
            x = (float) Math.floor(Math.random() * (screenWidth -  image.getWidth()));
            if (startPoint) {
                y = - image.getHeight();
                direction = "down";
            } else {
                y = screenHeight;
                direction = "up";
            }
        } else {
            y = (float) Math.floor(Math.random() * (screenHeight -  image.getHeight()));
            if (startPoint) {
                x = - image.getWidth();
                direction = "right";
            } else {
                x = screenWidth;
                direction = "left";
            }
        }
        image.setX(x);
        image.setY(y);
        image.setTag(direction);
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
