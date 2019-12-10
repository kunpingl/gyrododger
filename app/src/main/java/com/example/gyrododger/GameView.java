package com.example.gyrododger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.graphics.Paint;

public class GameView extends View {

    private Bitmap player;
    private Bitmap backgroundImage;
    private Paint scorePaint = new Paint();
    private Bitmap flyLife[] = new Bitmap[2];

    public GameView(Context context) {
        super(context);
        Bitmap temp_player = BitmapFactory.decodeResource(getResources(), R.drawable.geoff);
        player = Bitmap.createScaledBitmap(
                temp_player, 200, 200, false);

        backgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.universe);
        scorePaint.setColor(Color.WHITE);
        scorePaint.setTextSize(70);
        scorePaint.setTypeface(Typeface.DEFAULT_BOLD);
        scorePaint.setAntiAlias(true);

        flyLife[0] = BitmapFactory.decodeResource(getResources(), R.drawable.life);
        flyLife[1] = BitmapFactory.decodeResource(getResources(), R.drawable.loselife);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(backgroundImage, 0,0,null);
        canvas.drawBitmap(player, 0, 0,null);
        canvas.drawText("Score : ", 20, 60, scorePaint);

        canvas.drawBitmap(flyLife[0], 580, 10, null);
        canvas.drawBitmap(flyLife[0], 680, 10, null);
        canvas.drawBitmap(flyLife[0], 780, 10, null);

    }
}
