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

import java.util.List;
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
    private float playerR;
    private float playerT;
    private float playerB;

    private ImageView redBall;
    private float redBallX;
    private float redBallY;

    private Timer timer = new Timer();
    private Handler handler = new Handler();
    private Random random = new Random();

    private int spawnTimer = 0;
    private boolean firstSpawn = true;

    private List<Enemies> enemiesList;
    private List<ImageView> iamgeList;


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
        initializer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (Enemies eachEnemy : enemiesList) {
                            redBallLogic(eachEnemy);
                        }
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

    private boolean WallCheck(Enemies enemies, float x, float y) {
        if (x > screenWidth + enemies.getImage().getWidth() || x < - enemies.getImage().getWidth()) {
            return true;
        }
        if (y > screenHeight + enemies.getImage().getHeight() || x < - enemies.getImage().getHeight()) {
            return true;
        }
        return false;
    }

    private void redBallLogic(Enemies eachEnemy) {
        if (eachEnemy.getDirection().equals("left")) {
            eachEnemy.setSpawnX(eachEnemy.getSpawnX() - random.nextInt(100));
        } else if (eachEnemy.getDirection().equals("right")) {
            eachEnemy.setSpawnX(eachEnemy.getSpawnX() + random.nextInt(100));
        } else if (eachEnemy.getDirection().equals("up")) {
            eachEnemy.setSpawnY(eachEnemy.getSpawnY() - random.nextInt(100));
        } else {
            eachEnemy.setSpawnY(eachEnemy.getSpawnY() + random.nextInt(100));
        }

        float redCenterX = eachEnemy.getSpawnX() + redBall.getWidth() / 2;
        float redCenterY = eachEnemy.getSpawnY() + redBall.getHeight() / 2;

        if (CollisionCheck(redCenterX, redCenterY)) {
            enemiesList.remove(eachEnemy);
            SpawnEnemy(false, 0, 0);
        }

        if (WallCheck(eachEnemy, redCenterX, redCenterY)) {
            enemiesList.remove(eachEnemy);
            SpawnEnemy(false, 0, 0);
        }


//        float redCenterX = redBallX + redBall.getWidth() / 2;
//        float redCenterY = redBallY + redBall.getHeight() / 2;
//
//        if (CollisionCheck(redCenterX, redCenterY)) {
//            redBallY = screenHeight + 50;
//        }
//
//        if (redBallY > screenHeight) {
//            redBallY = -100;
//            redBallX = (float) Math.floor(Math.random() * (screenWidth - redBall.getWidth()));
//        }
//        redBall.setX(redBallX);
//        redBall.setY(redBallY);
    }

    private void initializer() {
        spawnTimer += 1;
        if (firstSpawn == true) {
            Double randomNum = Math.random();
            if (randomNum > 0.49) {
                SpawnEnemy(true, screenWidth + 2 * redBall.getHeight(), 0);
            } else {
                SpawnEnemy(true, -2 * redBall.getHeight(), 0);
            }
            randomNum = Math.random();
            if (randomNum > 0.49) {
                SpawnEnemy(true, 0, screenHeight - 2 * redBall.getHeight());
            } else {
                SpawnEnemy(true, 0, screenHeight - 2 * redBall.getHeight());
            }
            firstSpawn = false;
        }
        if (spawnTimer == 200) {
            SpawnEnemy(false, 0, 0);
        }
    }

    private Enemies SpawnEnemy(boolean isPreset, float x, float y) {
        float spawnY = (float) Math.floor(Math.random() * (screenHeight - 2 * redBall.getHeight()));
        float spawnX = (float) Math.floor(Math.random() * (screenWidth - 2 * redBall.getWidth()));
        spawnY += redBall.getHeight();
        spawnX += redBall.getWidth();

        Double verticiesA = Math.random();
        Double verticiesB = Math.random();

        String direction = "none";
        //Random generator = new Random();
        //int randomIndex = generator.nextInt(myArray.length);
        ImageView enemyImage = findViewById(R.id.redBall);
        if (!isPreset) {
            if (verticiesA > 0.49) {
                if (verticiesB > 0.49) {
                    spawnY = screenHeight + 2 * redBall.getHeight();
                } else {
                    spawnY = -2 * redBall.getHeight();
                }
            } else {
                if (verticiesB > 0.49) {
                    spawnX = screenWidth + 2 * redBall.getWidth();
                } else {
                    spawnX = -2 * redBall.getWidth();
                }
            }
        } else {
            if (x == 0) {
                spawnY = y;
            } else if (y == 0) {
                spawnX = x;
            }
        }
        if (spawnX > screenWidth) {
            direction = "left";
        } else if (spawnX < 0) {
            direction = "right";
        } else if (spawnY > screenHeight) {
            direction = "up";
        } else if (spawnY < 0) {
            direction = "down";
        }
        Enemies newEnemy = new Enemies(spawnX, spawnY, direction, enemyImage);
        enemiesList.add(newEnemy);
        return newEnemy;
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
            System.out.println("playerX = " + playerX + " compareWith " + playerL);
            System.out.println("playerY = " + playerY + " compareWith " + playerT);
            return true;
        }
    };
}
