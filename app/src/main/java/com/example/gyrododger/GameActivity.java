package com.example.gyrododger;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    private SoundPlayer soundPlayer;
    private Chronometer chronometer;

    private int screenWidth;
    private int screenHeight;

    private ImageView player;
    private int playerX;
    private int playerY;

    private int timer_speed = 0;
    private int factor_speed = 5;
    private boolean golden_flg;

    private ImageView explosion;
    private ImageView survivalTime_sec;
    private ImageButton next;

    private int life = 2; // 3 hits game over.
    private ImageView life1;
    private ImageView life2;
    private ImageView life3;
    private ImageView lostLife1;
    private ImageView lostLife2;
    private ImageView lostLife3;

    private int gameStatus = 0; // 0 as inGaming and -1 as End
    private MediaPlayer song;


    private Timer timer = new Timer();
    private Handler handler = new Handler();
    private Random random = new Random();
    private List<ImageView> ballList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        song = MediaPlayer.create(GameActivity.this, R.raw.music);
        song.start();
        song.setLooping(true);
        
        player = findViewById(R.id.player);
        player.setOnTouchListener(movingEventListener);

        initiateGame();

        for (ImageView eachBall : ballList) { // create balls
            ballSpawnLogic(eachBall);
        }

        gameStatus = 0; // 0: gaming 1: end

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // dead, gameOver.
                        if (gameStatus == -1) {
                            gameOver();
                            return;
                        }
                        timer_speed++;
                        speedTimer();
                        for (ImageView eachBall : ballList) {
                            ballMovingLogic(eachBall);
                        }
                    }
                });
            }
        }, 0, 5);
    }

    private void endUi() {

        TextView showTime = findViewById(R.id.showTime);
        showTime.setText(chronometer.getText().toString());
        survivalTime_sec.setX(45f);
        survivalTime_sec.setY(200f);
        showTime.setX(410f);
        showTime.setY(1000f);
        next.setX(410f);
        next.setY(1400f);

        showTime.setBackgroundColor(Color.parseColor("#FF69B4"));
        showTime.setTextColor(Color.parseColor("#F8F8FF"));
        showTime.setVisibility(View.VISIBLE);
        survivalTime_sec.setVisibility(View.VISIBLE);
        next.setVisibility(View.VISIBLE);

        next.setOnClickListener(unused -> {
            song.stop();
            finish();
        });
    }

    private void gameOver() {

        explosion.setX(player.getX());
        explosion.setY(player.getY());
        player.setVisibility(View.GONE);
        explosion.setVisibility(View.VISIBLE);

        chronometer.stop();
        endUi();
    }

    private void initiateGame() {
        soundPlayer = new SoundPlayer(this);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        ballList = new ArrayList<>();

        next = findViewById(R.id.next);
        next.setVisibility(View.INVISIBLE);
        explosion = findViewById(R.id.explosion);
        explosion.setVisibility(View.INVISIBLE);
        survivalTime_sec = findViewById(R.id.survivalTime_sec);
        survivalTime_sec.setVisibility(View.INVISIBLE);

        chronometer = findViewById(R.id.chronometer);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();

        life1 = findViewById(R.id.life1);
        life2 = findViewById(R.id.life2);
        life3 = findViewById(R.id.life3);
        lostLife1 = findViewById(R.id.lostLife1);
        lostLife2 = findViewById(R.id.lostLife2);
        lostLife3 = findViewById(R.id.lostLife3);

        ImageView redBall1 = findViewById(R.id.redBall1);
        ImageView redBall2 = findViewById(R.id.redBall2);
        ImageView redBall3 = findViewById(R.id.redBall3);
        ImageView redBall4 = findViewById(R.id.redBall4);
        ImageView redBall5 = findViewById(R.id.redBall5);
        ImageView goldenBall = findViewById(R.id.goldenBall);

        ballList.add(redBall1);
        ballList.add(redBall2);
        ballList.add(redBall3);
        ballList.add(redBall4);
        ballList.add(redBall5);

        ballList.add(goldenBall);
    }


    private boolean collisionCheck(ImageView view, float l, float r, float t, float b) {
        if (r >= player.getLeft() && r <= player.getRight()) {
            if (t < player.getBottom() && b > player.getTop()) {
                if (ballChecker(view) == 1) { // golden ball gets caught by player
                    factor_speed -= 10;
                    golden_flg = false;
                } else if (ballChecker(view) == 0) { // red balls hit player
                    lifeChanger();
                    if (life <= 0) {
                        gameStatus = -1;
                        soundPlayer.playExplosionSound();
                    } else {
                        soundPlayer.playHitSound();
                        life--;
                    }
                }
                return true;
            }
        }
        return false;
    }

    private void lifeChanger() {
        switch (life) {
            case 2:
                life1.setVisibility(View.INVISIBLE);
                lostLife1.setVisibility(View.VISIBLE);
                break;
            case 1:
                life2.setVisibility(View.INVISIBLE);
                lostLife2.setVisibility(View.VISIBLE);
                break;
            case 0:
                life3.setVisibility(View.INVISIBLE);
                lostLife3.setVisibility(View.VISIBLE);
                break;
        }
    }

    private boolean wallChecker(ImageView view, float x, float y) {
        if (x < - 2 * view.getWidth() || x > screenWidth + view.getWidth()
            || y < - 2 * view.getHeight() || y > screenHeight + view.getHeight()) {
            if (ballChecker(view) == 1) { // golden ball goes beyond any screen edge
                golden_flg = false; // golden ball would not be created for a while
            }
            return true;
        }
        return false;
    }

    private void speedTimer() {
        System.out.println(timer_speed);
        if (timer_speed % 350 == 0) {
            factor_speed += 2 ;
        }
        // about 10 sec
        if (timer_speed % 500 == 0 && factor_speed > 20) {
            golden_flg = true; // golden ball begins to be created and move
        }
    }

    /**
     * to determine the type of balls
     * @param view the input imageView being checked
     * @return 0 as red, 1 as golden.
     */
    private int ballChecker(ImageView view) {
        switch (view.getId()) {
            case R.id.goldenBall:
                return 1;
            default:
                return 0;
        }
    }

    private void ballMovingLogic(ImageView view) {

        if (ballChecker(view) == 1 && !golden_flg) {
            return;
        }

        float ballLeft = view.getX();
        float ballRight = view.getX() + view.getWidth();
        float ballTop = view.getY();
        float ballBottom = view.getY() + view.getHeight();

        // before move
        int newY = (int) view.getY();
        int newX = (int) view.getX();



        if (ballChecker(view) == 0) { // red ball moving logic
            float speed = random.nextFloat() * factor_speed;
            if (view.getTag().equals("down")) {
                newY += speed;
            } else if (view.getTag().equals("up")) {
                newY -= speed;
            } else if (view.getTag().equals("left")) {
                newX -= speed;
            } else if (view.getTag().equals("right")) {
                newX += speed;
            }
        } else { // golden moving logic
            int speed = random.nextInt(30);
            if (view.getTag().equals("down")) {
                newY += speed;
            } else if (view.getTag().equals("up")) {
                newY -= speed;
            } else if (view.getTag().equals("left")) {
                newX -= speed;
            } else if (view.getTag().equals("right")) {
                newX += speed;
            }
        }

        if (collisionCheck(view, ballLeft, ballRight, ballTop, ballBottom)) { // player gets hit
            ballSpawnLogic(view);
        } else if (wallChecker(view, ballLeft, ballTop)) { // go beyond the screen edges
            ballSpawnLogic(view);
        } else { // still inside the screen
            view.setY(newY);
            view.setX(newX);
        }


    }

    private void ballSpawnLogic(ImageView image) {
        String direction;
        float x, y;
        boolean horiOrVerti = random.nextBoolean(); //true is Vertical and false is Horizontal
        boolean startPoint = random.nextBoolean(); //true is start from 0 and false otherwise

        if (horiOrVerti) {
            //move vertically
            x = (float) Math.floor(Math.random() * (screenWidth -  image.getWidth()));
            if (startPoint) {
                y = -100.0f; // outside the upper screen edges
                direction = "down";
            } else {
                y = screenHeight + 100.0f; // outside the lower screen edges
                direction = "up";
            }
        } else {
            y = (float) Math.floor(Math.random() * (screenHeight -  image.getHeight()));
            if (startPoint) {
                x = -100.0f; // outside the left screen edges
                direction = "right";
            } else {
                x = screenWidth + 100.0f; // outside the right screen edges
                direction = "left";
            }
        }
        image.setX(x);
        image.setY(y);
        image.setTag(direction);
    }

    private void boundChecker(View view, int view_left, int view_right, int view_top, int view_bottom) {
        // The player can not pass over the bound of screen
        if (view_left < 0) { // left edge of view reaches the left edge of screen
            view_left = 0;
            view_right = view_left + view.getWidth();
        }

        if (view_right > screenWidth) { // right edge of view reaches the right edge of screen
            view_right = screenWidth;
            view_left = view_right - view.getWidth();
        }

        if (view_top < 0) { // top edge of view reaches the top edge of screen
            view_top = 0;
            view_bottom = view_top + view.getHeight();
        }

        if (view_bottom > screenHeight) { // bottom edge of view reaches the bottom edge of screen
            view_bottom = screenHeight;
            view_top = view_bottom - view.getHeight();
        }

        view.layout(view_left, view_top, view_right, view_bottom); // set the location of view
    }


    private OnTouchListener movingEventListener = new OnTouchListener() {
        int xDelta;
        int yDelta;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (gameStatus == -1) { // if game ends, no move is allowed
                return false;
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    playerX = (int) event.getRawX(); //get the coordinates of finger's touching
                    playerY = (int) event.getRawY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    xDelta = (int) event.getRawX() - playerX;
                    yDelta = (int) event.getRawY() - playerY;

                    int view_left = view.getLeft() + xDelta;
                    int view_top = view.getTop() + yDelta;
                    int view_right = view.getRight() + xDelta;
                    int view_bottom = view.getBottom() + yDelta;

                    boundChecker(view, view_left, view_right, view_top, view_bottom);

                    playerX = (int) event.getRawX();
                    playerY = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return true;
        }
    };
}
