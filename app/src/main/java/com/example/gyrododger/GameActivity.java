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

    private int timeCount = 0;
    private int factor = 15;
    private boolean golden_flg;

    private ImageView explosion;
    private ImageView showTimeText;
    private ImageButton next;

    private int life = 2; // 3 hit game over.
    private ImageView life1;
    private ImageView life2;
    private ImageView life3;
    private ImageView lostLife1;
    private ImageView lostLife2;
    private ImageView lostLife3;

    private int gameStatus = 0; // 0 as inGaming and -1 as End
    private MediaPlayer ring;


    private Timer timer = new Timer();
    private Handler handler = new Handler();
    private Random random = new Random();
    private List<ImageView> enemyList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        ring = MediaPlayer.create(GameActivity.this, R.raw.music);
        ring.start();
        ring.setLooping(true);

        soundPlayer = new SoundPlayer(this);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        enemyList = new ArrayList<>();

        player = findViewById(R.id.player);
        player.setOnTouchListener(movingEventListener);

        initiateGame();

        for (ImageView eachEnemy : enemyList) {
            ballSpawnLogic(eachEnemy);
        }

        gameStatus = 0;

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
                        timeCount++;
                        System.out.println(timeCount);
                        timeChecker();
                        for (ImageView eachEnemy : enemyList) {
                            ballMovingLogic(eachEnemy);
                        }
                    }
                });
            }
        }, 0, 5);
    }

    private void endUi() {

        TextView showTime = findViewById(R.id.showTime);
        showTime.setText(chronometer.getText().toString());
        showTimeText.setX(45f);
        showTimeText.setY(200f);
        showTime.setX(410f);
        showTime.setY(1000f);
        next.setX(410f);
        next.setY(1400f);

        showTime.setBackgroundColor(Color.parseColor("#FF69B4"));
        showTime.setTextColor(Color.parseColor("#F8F8FF"));
        showTime.setVisibility(View.VISIBLE);
        showTimeText.setVisibility(View.VISIBLE);
        next.setVisibility(View.VISIBLE);

        next.setOnClickListener(unused -> {
            ring.stop();
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

/*        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //explosion.setVisibility(View.GONE);
                //killImageView();
                ring.stop();
                finish();
            }
        }, 1000000);*/

    }



    private void killImageView() {

        chronometer.setVisibility(View.GONE);

        life1.setVisibility(View.GONE);
        life2.setVisibility(View.GONE);
        life3.setVisibility(View.GONE);

        lostLife1.setVisibility(View.GONE);
        lostLife2.setVisibility(View.GONE);
        lostLife3.setVisibility(View.GONE);

        for (ImageView eachBall : enemyList) {
            eachBall.setVisibility(View.GONE);
        }
    }


    private void initiateGame() {
        next = findViewById(R.id.next);
        next.setVisibility(View.INVISIBLE);
        explosion = findViewById(R.id.explosion);
        explosion.setVisibility(View.INVISIBLE);
        showTimeText = findViewById(R.id.showTimeText);
        showTimeText.setVisibility(View.INVISIBLE);

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

        enemyList.add(redBall1);
        enemyList.add(redBall2);
        enemyList.add(redBall3);
        enemyList.add(redBall4);
        enemyList.add(redBall5);

        enemyList.add(goldenBall);
    }


    private boolean collisionCheck(ImageView view, float l, float r, float t, float b) {
        if (r >= player.getLeft() && r <= player.getRight()) {
            if (t < player.getBottom() && b > player.getTop()) {
                if (ballChecker(view) == 1) {
                    factor -= 10;
                    golden_flg = false;
                } else if (ballChecker(view) == 0) {
                    lifeChecker();
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

    private void lifeChecker() {
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

    private boolean wallCheck(ImageView view, float x, float y) {
        if (x < - 2 * view.getWidth() || x > screenWidth + view.getWidth()
            || y < - 2 * view.getHeight() || y > screenHeight + view.getHeight()) {
            if (ballChecker(view) == 1) {
                golden_flg = false;
            }
            return true;
        }
        return false;
    }

    private void timeChecker() {
        System.out.println(timeCount);
        if (timeCount % 350 == 0) {
            factor += 2 ;
        }
        // about 10 sec
        if (timeCount % 500 == 0 && factor > 20) {
            golden_flg = true;
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

        int newY = (int) view.getY();
        int newX = (int) view.getX();



        if (ballChecker(view) == 0) {
            float speed = random.nextFloat() * factor;
            if (view.getTag().equals("down")) {
                newY += speed;
            } else if (view.getTag().equals("up")) {
                newY -= speed;
            } else if (view.getTag().equals("left")) {
                newX -= speed;
            } else if (view.getTag().equals("right")) {
                newX += speed;
            }
        } else {
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

        if (collisionCheck(view, ballLeft, ballRight, ballTop, ballBottom)) {
            ballSpawnLogic(view);
        } else if (wallCheck(view, ballLeft, ballTop)) {
            ballSpawnLogic(view);
        } else {
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
                x = screenWidth + 100.0f;
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
            if (gameStatus == -1) {
                return false;
            }
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
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return true;
        }
    };
}
