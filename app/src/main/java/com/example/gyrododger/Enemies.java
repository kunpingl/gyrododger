package com.example.gyrododger;

import android.widget.ImageView;

public class Enemies {

    private float spawnX;
    private float spawnY;
    private String direction;
    private ImageView image;

    public Enemies(float spawnX, float spawnY, String direction, ImageView image) {
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.direction = direction;
        this.image = image;
    }

    public float getSpawnX() {
        return spawnX;
    }

    public float getSpawnY() {
        return spawnY;
    }

    public String getDirection() {
        return direction;
    }

    public ImageView getImage() {
        return image;
    }

    public void setSpawnX(float spawnX) {
        this.spawnX = spawnX;
    }

    public void setSpawnY(float spawnY) {
        this.spawnY = spawnY;
    }
}
