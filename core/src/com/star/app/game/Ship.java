package com.star.app.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.star.app.screen.ScreenManager;

public class Ship {
    protected Vector2 position;
    protected Vector2 velocity;
    protected float angle;
    protected float enginePower;
    protected float fireTimer;
    protected int hpMax;
    protected int hp;
    protected Circle hitArea;
    protected Weapon currentWeapon;
    protected Weapon[] weapons;
    protected int weaponNum;
    protected TextureRegion texture;
    protected GameController gc;
    protected OwnerType ownerType;
    protected final float BASE_SIZE = 64;
    protected final float BASE_RADIUS = BASE_SIZE / 2;

    public OwnerType getOwnerType() {
        return ownerType;
    }

    public Ship(GameController gc, int hpMax, float enginePower) {
        this.gc = gc;
        this.hpMax = hpMax;
        this.hp = hpMax;
        this.angle = 0.0f;
        this.enginePower = enginePower;
        createWeapons();
        this.currentWeapon = weapons[weaponNum];
    }

    public Circle getHitArea() {
        return hitArea;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getAngle() {
        return angle;
    }

    public Weapon getCurrentWeapon() {
        return currentWeapon;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    private void createWeapons() {
        weapons = new Weapon[]{
                new Weapon(gc, this, "Laser", 0.2f, 1, 300, 300,
                        new Vector3[]{
                                new Vector3(28, 90, 0),
                                new Vector3(28, -90, 0)
                        }),
                new Weapon(gc, this, "Laser", 0.2f, 1, 600, 500,
                        new Vector3[]{
                                new Vector3(28, 0, 0),
                                new Vector3(28, 90, 20),
                                new Vector3(28, -90, -20)
                        }),
                new Weapon(gc, this, "Laser", 0.1f, 1, 600, 1000,
                        new Vector3[]{
                                new Vector3(28, 0, 0),
                                new Vector3(28, 90, 20),
                                new Vector3(28, -90, -20)
                        }),
                new Weapon(gc, this, "Laser", 0.1f, 2, 600, 1000,
                        new Vector3[]{
                                new Vector3(28, 90, 0),
                                new Vector3(28, -90, 0),
                                new Vector3(28, 90, 15),
                                new Vector3(28, -90, -15)
                        }),
                new Weapon(gc, this, "Laser", 0.1f, 3, 600, 1500,
                        new Vector3[]{
                                new Vector3(28, 0, 0),
                                new Vector3(28, 90, 10),
                                new Vector3(28, -90, -10),
                                new Vector3(28, 90, 20),
                                new Vector3(28, -90, -20)
                        })
        };
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x - BASE_RADIUS, position.y - BASE_RADIUS,
                BASE_RADIUS, BASE_RADIUS, BASE_SIZE, BASE_SIZE,
                1, 1, angle);
    }

    public void takeDamage(int amount) {
        hp -= amount;
    }

    public void update(float dt) {
        fireTimer += dt;
        position.mulAdd(velocity, dt);
        hitArea.setPosition(position);
        float stopRatio = 1.0f - 0.8f * dt;
        if (stopRatio < 0.0f) {
            stopRatio = 0.0f;
        }
        velocity.scl(stopRatio);
        checkSpaceBorder();
    }

    private void checkSpaceBorder() {
        if (position.x < 32) {
            position.x = 32;
            velocity.x *= -0.5f;
        }

        if (position.x > ScreenManager.SCREEN_WIDTH - 32f) {
            position.x = ScreenManager.SCREEN_WIDTH - 32f;
            velocity.x *= -0.5f;
        }

        if (position.y < 32) {
            position.y = 32;
            velocity.y *= -0.5f;
        }

        if (position.y > ScreenManager.SCREEN_HEIGHT - 32f) {
            position.y = ScreenManager.SCREEN_HEIGHT - 32f;
            velocity.y *= -0.5f;
        }
    }

    public void tryToFire() {
        if (fireTimer > currentWeapon.getFirePeriod()) {
            fireTimer = 0.0f;
            currentWeapon.fire();
        }
    }

    public void accelerate(float dt) {
        velocity.x += MathUtils.cosDeg(angle) * enginePower * dt;
        velocity.y += MathUtils.sinDeg(angle) * enginePower * dt;
    }

    public void decelerate(float dt) {
        velocity.x -= MathUtils.cosDeg(angle) * enginePower / 2 * dt;
        velocity.y -= MathUtils.sinDeg(angle) * enginePower / 2 * dt;
    }
}
