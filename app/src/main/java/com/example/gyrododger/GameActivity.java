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
    private Random random = new Random();

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

        addBall();


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

    private void addBall() {
        ImageView redBall1 = findViewById(R.id.redBall1);
        ImageView redBall2 = findViewById(R.id.redBall2);
        ImageView redBall3 = findViewById(R.id.redBall3);
        ImageView redBall4 = findViewById(R.id.redBall4);
        ImageView redBall5 = findViewById(R.id.redBall5);

        enemyList.add(redBall1);
        enemyList.add(redBall2);
        enemyList.add(redBall3);
        enemyList.add(redBall4);
        enemyList.add(redBall5);
    }


    private boolean CollisionCheck(float l, float r, float t, float b) {
        if (r >= player.getLeft() && r <= player.getRight()) {
            if (t < player.getBottom() && b > player.getTop()) {
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

        if (enemy.getTag().equals("down")) {
            newY += 10.0f;
        } else if (enemy.getTag().equals("up")) {
            newY -= 10.0f;
        } else if (enemy.getTag().equals("left")) {
            newX -= 10.0f;
        } else if (enemy.getTag().equals("right")) {
            newX += 10.0f;
        }

        if (CollisionCheck(redL, redR, redT, redB)) {
            respawnEnemy(enemy);
        } else if (WallCheck(enemy, redL, redT)) {
            respawnEnemy(enemy);
        } else {
            enemy.setY(newY);
            enemy.setX(newX);
        }


    }

    private void respawnEnemy(ImageView image) {
        String direction;
        float x, y;
        boolean horiOrVerti = random.nextBoolean(); //true is Vertical and false is Horizontal
        boolean startPoint = random.nextBoolean(); //true is start from 0 and false otherwise
        if (horiOrVerti) {
            //move vertically
            x = (float) Math.floor(Math.random() * (screenWidth -  image.getWidth()));
            if (startPoint) {
                y = -100.0f;
                direction = "down";
            } else {
                y = screenHeight + 100.0f;
                direction = "up";
            }
        } else {
            y = (float) Math.floor(Math.random() * (screenHeight -  image.getHeight()));
            if (startPoint) {
                x = -100.0f;
                direction = "right";
            } else {
                x = screenWidth - 100.0f;
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
