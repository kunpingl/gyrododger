package com.example.gyrododger;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import java.lang.reflect.Field;


public class GameActivity extends Activity {

    private int screenWidth;
    private int screenHeight;
    private ImageView flight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        flight = findViewById(R.id.flight);
        flight.setOnTouchListener(movingEventListener);
        flight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // click event
            }
        });
    }


    private OnTouchListener movingEventListener = new OnTouchListener() {
        int lastX, lastY, x, y;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int dx = (int) event.getRawX() - lastX;
                    int dy = (int) event.getRawY() - lastY;

                    int left = v.getLeft() + dx;
                    int top = v.getTop() + dy;
                    int right = v.getRight() + dx;
                    int bottom = v.getBottom() + dy;
                    
                    // The flight can not pass over the bound of screen
                    if (left < 0) {
                        left = 0;
                        right = left + v.getWidth();
                    }

                    if (right > screenWidth) {
                        right = screenWidth;
                        left = right - v.getWidth();
                    }

                    if (top < 0) {
                        top = 0;
                        bottom = top + v.getHeight();
                    }

                    if (bottom > screenHeight) {
                        bottom = screenHeight;
                        top = bottom - v.getHeight();
                    }

                    v.layout(left, top, right, bottom);

                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();

                    break;
                case MotionEvent.ACTION_UP:
                    //check the moving distance to determine if it is a click event
                    if (Math.abs(event.getRawX() - x) < 10 && Math.abs(event.getRawY() - y) < 10) {
                        try {
                            Field field = View.class.getDeclaredField("mListenerInfo");
                            field.setAccessible(true);
                            Object object = field.get(v);
                            field = object.getClass().getDeclaredField("mOnClickListener");
                            field.setAccessible(true);
                            object = field.get(object);
                            if (object != null && object instanceof View.OnClickListener) {
                                ((View.OnClickListener) object).onClick(v);
                            }
                        } catch (Exception e) {
                        }
                    }
                    break;
            }
            return true;
        }
    };
}
