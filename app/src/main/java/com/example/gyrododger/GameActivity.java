package com.example.gyrododger;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class GameActivity extends Activity {

    private int screenWidth;
    private int screenHeight;
    private ImageView flight;

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

        flight = findViewById(R.id.flight);
        flight.setOnTouchListener(movingEventListener);
    }

    private void setBound(View view, int leftBound, int rightBound, int topBound, int bottomBound) {
        // The flight can not pass over the bound of screen
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
        int xCoordinate; // current X coordinate
        int yCoordinate; // current Y coordinate
        int xDelta;
        int yDelta;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    xCoordinate = (int) event.getRawX();
                    yCoordinate = (int) event.getRawY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    xDelta = (int) event.getRawX() - xCoordinate;
                    yDelta = (int) event.getRawY() - yCoordinate;

                    int leftBound = view.getLeft() + xDelta;
                    int topBound = view.getTop() + yDelta;
                    int rightBound = view.getRight() + xDelta;
                    int bottomBound = view.getBottom() + yDelta;

                    setBound(view, leftBound, rightBound, topBound, bottomBound);

                    xCoordinate = (int) event.getRawX();
                    yCoordinate = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return true;
        }
    };
}
