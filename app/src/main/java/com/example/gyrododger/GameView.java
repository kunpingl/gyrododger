package com.example.gyrododger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

public class GameView extends View {

    private Bitmap player;
    private Bitmap backgroundImage;

    public GameView(Context context) {
        super(context);
        Bitmap temp_player = BitmapFactory.decodeResource(getResources(), R.drawable.player);
        player = Bitmap.createScaledBitmap(
                temp_player, 200, 200, false);

        backgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.universe);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(backgroundImage, 0,0,null);
        canvas.drawBitmap(player, 0, 0,null);

    }
}
