package com.star.app.game;

import com.badlogic.gdx.math.Vector2;
import com.star.app.game.helpers.Poolable;

public class Asteroid implements Poolable {
    private Vector2 position;
    private Vector2 velocity;
    float scale;
    private boolean active;

    @Override
    public boolean isActive() {
        return active;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public Asteroid() {
//        this.position = new Vector2(ScreenManager.SCREEN_WIDTH, MathUtils.random(0, ScreenManager.SCREEN_HEIGHT));
        this.position = new Vector2(0, 0);
//        this.velocity = new Vector2(MathUtils.random(-4, -30), 0);
        this.velocity = new Vector2(0, 0);
//        this.scale = Math.abs(velocity.x) / 40f * 0.8f;
        this.active = false;
    }

    public void deactivate() {
        active = false;
    }

    public void update(float dt) {
        position.mulAdd(velocity, dt);
        if (position.x < -128) {
            deactivate();
        }
    }

    public void activate(float x, float y, float vx, float vy) {
        position.set(x, y);
        velocity.set(vx, vy);
        active = true;
    }
}
