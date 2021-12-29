package com.star.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.star.app.screen.ScreenManager;
import com.star.app.screen.utils.Assets;
import com.sun.tools.javac.comp.Todo;

public class Hero {
    private TextureRegion texture;
    private Vector2 position;
    private Vector2 velocity;
    private float angle;
    private float enginePower;
    private float fireTimer;
    private GameController gc;
    private int score;
    private int scoreView;
    private int hp;
    private Circle hitArea;
    private final float BASE_SIZE = 64;
    private final float BASE_RADIUS = BASE_SIZE / 2;

    public int getHp() {
        return hp;
    }

    public Circle getHitArea() {
        return hitArea;
    }

    public int getScoreView() {
        return scoreView;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void addScore(int amount) {
        score += amount;
    }

    public Hero(GameController gc) {
        this.gc = gc;
        this.texture = Assets.getInstance().getAtlas().findRegion("ship");
        this.position = new Vector2(ScreenManager.SCREEN_WIDTH / 2, ScreenManager.SCREEN_HEIGHT / 2);
        this.velocity = new Vector2(0, 0);
        this.angle = 0.0f;
        this.enginePower = 500.0f;
        this.hitArea = new Circle();
        this.hp = 3;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x - BASE_RADIUS, position.y - BASE_RADIUS,
                BASE_RADIUS, BASE_RADIUS, BASE_SIZE, BASE_SIZE,
                1, 1, angle);
    }

    public void takeDamage(int amount) {
        hp -= amount;
        if (hp <= 0) {
            //TODO сделать новый экран "Game Over"
        }
    }

    public void update(float dt) {
        fireTimer += dt;
        hitArea.setPosition(position);
        hitArea.setRadius(BASE_RADIUS);
        if (scoreView < score) {
            scoreView += 2000 * dt;
            if (scoreView > score) {
                scoreView = score;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            if (fireTimer > 0.2f) {
                fireTimer = 0.0f;
                float wx = position.x + MathUtils.cosDeg(angle + 90) * 20;
                float wy = position.y + MathUtils.sinDeg(angle + 90) * 20;
                gc.getBulletController().setup(wx, wy,
                        MathUtils.cosDeg(angle) * 500.0f + velocity.x,
                        MathUtils.sinDeg(angle) * 500.0f + velocity.y);
                wx = position.x + MathUtils.cosDeg(angle - 90) * 20;
                wy = position.y + MathUtils.sinDeg(angle - 90) * 20;
                gc.getBulletController().setup(wx, wy,
                        MathUtils.cosDeg(angle) * 500.0f + velocity.x,
                        MathUtils.sinDeg(angle) * 500.0f + velocity.y);
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            angle += 180.0f * dt;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            angle -= 180.0f * dt;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            velocity.x += MathUtils.cosDeg(angle) * enginePower * dt;
            velocity.y += MathUtils.sinDeg(angle) * enginePower * dt;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            velocity.x -= MathUtils.cosDeg(angle) * enginePower / 2 * dt;
            velocity.y -= MathUtils.sinDeg(angle) * enginePower / 2 * dt;
        }

        position.mulAdd(velocity, dt);

        float stopRatio = 1.0f - 0.8f * dt;
        if (stopRatio < 0.0f) {
            stopRatio = 0.0f;
        }
        velocity.scl(stopRatio);

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
}
