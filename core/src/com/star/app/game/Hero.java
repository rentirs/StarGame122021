package com.star.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.star.app.screen.ScreenManager;
import com.star.app.screen.utils.Assets;

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
    private int hpMax;
    private int hp;
    private Circle hitArea;
    private StringBuilder sb;
    private Weapon currentWeapon;
    private int money;
    private final float BASE_SIZE = 64;
    private final float BASE_RADIUS = BASE_SIZE / 2;

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

    public void addScore(int amount) {
        score += amount;
    }

    public Hero(GameController gc) {
        this.gc = gc;
        this.texture = Assets.getInstance().getAtlas().findRegion("ship");
        this.position = new Vector2(ScreenManager.SCREEN_WIDTH / 2, ScreenManager.SCREEN_HEIGHT / 2);
        this.velocity = new Vector2();
        this.angle = 0.0f;
        this.enginePower = 500.0f;
        this.hpMax = 100;
        this.hp = hpMax;
        this.sb = new StringBuilder();
        this.currentWeapon = new Weapon(gc, this, "Laser", 0.1f, 1, 600, 300,
                new Vector3[]{
                        new Vector3(28, 0, 0),
                        new Vector3(28, 90, 20),
                        new Vector3(28, -90, -20)
        });
        this.hitArea = new Circle(position, 29);
    }

    public void renderGUI(SpriteBatch batch, BitmapFont font) {
        sb.setLength(0);
        sb.append("SCORE: ").append(scoreView).append("\n");
        sb.append("HP: ").append(hp).append(" / ").append(hpMax).append("\n");
        sb.append("BULLETS: ").append(currentWeapon.getCurBullets()).append(" / ").append(currentWeapon.getMaxBullets()).append("\n");
        sb.append("MONEY: ").append(money);
        font.draw(batch, sb, 20, 700);
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
        updateScore(dt);

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            tryToFire();
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

            float bx = position.x + MathUtils.cosDeg(angle + 180) * 20;
            float by = position.y + MathUtils.sinDeg(angle + 180) * 20;
            for (int i = 0; i < 3; i++) {
                gc.getParticleController().setup(
                        bx + MathUtils.random(-4, 4),
                        by + MathUtils.random(-4, 4),
                        velocity.x * -0.3f + MathUtils.random(-20, 20),
                        velocity.y * -0.3f + MathUtils.random(-20, 20),
                        0.5f, 1.2f, 0.2f,
                        1, 0.5f, 0, 1,
                        1, 1 , 1, 1);
            }

        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            velocity.x -= MathUtils.cosDeg(angle) * enginePower / 2 * dt;
            velocity.y -= MathUtils.sinDeg(angle) * enginePower / 2 * dt;

            float bx = position.x + MathUtils.cosDeg(angle + 90) * 20;
            float by = position.y + MathUtils.sinDeg(angle + 90) * 20;
            backFire(bx, by);
            bx = position.x + MathUtils.cosDeg(angle - 90) * 20;
            by = position.y + MathUtils.sinDeg(angle - 90) * 20;
            backFire(bx, by);

        }

        position.mulAdd(velocity, dt);
        hitArea.setPosition(position);

        float stopRatio = 1.0f - 0.8f * dt;
        if (stopRatio < 0.0f) {
            stopRatio = 0.0f;
        }
        velocity.scl(stopRatio);

        checkSpaceBorder();
    }

    private void backFire(float bx, float by) {
        for (int i = 0; i < 2; i++) {
            gc.getParticleController().setup(
                    bx + MathUtils.random(-4, 4),
                    by + MathUtils.random(-4, 4),
                    velocity.x * 0.1f + MathUtils.random(-20, 20),
                    velocity.y * 0.1f + MathUtils.random(-20, 20),
                    0.4f, 1.2f, 0.2f,
                    1, 0.5f, 0, 1,
                    1, 1 , 1, 1);
        }
    }

    private void updateScore(float dt) {
        if (scoreView < score) {
            scoreView += 2000 * dt;
            if (scoreView > score) {
                scoreView = score;
            }
        }
    }

    private void tryToFire() {
        if (fireTimer > currentWeapon.getFirePeriod()) {
            fireTimer = 0.0f;
            currentWeapon.fire();
        }
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

    public void consume(PowerUp powerUp) {
        switch (powerUp.getType()){
            case ENERGY:
                hp += powerUp.getPower();
                break;
            case MONEY:
                money += powerUp.getPower();
                break;
            case AMMO:
                currentWeapon.addAmmo(powerUp.getPower());
                break;
        }
    }
}
