package com.star.app.game;

import com.badlogic.gdx.math.Vector2;
import com.star.app.game.helpers.Poolable;
import com.star.app.screen.ScreenManager;

public class Bullet implements Poolable {
    private Vector2 position;
    private Vector2 velocity;
    private boolean active;
    private GameController gameController;
    private Ship owner;

    public Ship getOwner()  {
        return owner;
    }

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

    public Bullet(GameController gameController) {
        this.gameController = gameController;
        this.position = new Vector2();
        this.velocity = new Vector2();
        this.active = false;
    }

    public void deactivate() {
        active = false;
    }

    public void update(float dt) {
        position.mulAdd(velocity, dt);
        if (position.x <= -20 ||
                position.y <= -20 ||
                position.x >= ScreenManager.SCREEN_WIDTH + 20 ||
                position.y >= ScreenManager.SCREEN_HEIGHT + 20) {
            deactivate();
        }

        gameController.getParticleController().getEffectBuilder().createBulletTrace(owner.getOwnerType(), this);
    }

    public void activate(Ship owner, float x, float y, float vx, float vy) {
        position.set(x, y);
        velocity.set(vx, vy);
        active = true;
        this.owner = owner;
    }
}
