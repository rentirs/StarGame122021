package com.star.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.star.app.screen.ScreenManager;
import com.star.app.screen.utils.Assets;

public class Hero extends Ship {
    public enum Skill {
        HP_MAX(20, 10),
        HP(20, 10),
        WEAPON(100),
        MAGNET(50, 10);

        final int cost;
        int power;
        Skill(int cost) {
            this.cost = cost;
        }

        Skill(int cost, int power) {
            this.cost = cost;
            this.power = power;
        }
    }

    private int score;
    private int scoreView;
    private final Circle magneticField;
    private final StringBuilder sb;
    private int money;
    private final Shop shop;

    public Circle getMagneticField() {
        return magneticField;
    }

    public void setPause(boolean pause) {
        gc.setPause(pause);
    }

    public Shop getShop() {
        return shop;
    }

    public int getScore() {
        return score;
    }

    public int getMoney() {
        return money;
    }

    public void addScore(int amount) {
        score += amount;
    }

    public boolean isMoneyEnough(int amount) {
        return money >= amount;
    }

    public void decreaseMoney(int amount) {
        money -= amount;
    }

    public Hero(GameController gc) {
        super(gc, 100, 500);
        this.shop = new Shop(this);
        this.position = new Vector2(ScreenManager.SCREEN_WIDTH / 2.0f, ScreenManager.SCREEN_HEIGHT / 2.0f);
        this.velocity = new Vector2();
        this.sb = new StringBuilder();
        this.magneticField = new Circle(position, 100);
        this.money = 150;
        this.texture = Assets.getInstance().getAtlas().findRegion("ship2");
        this.hitArea = new Circle(position, 29);
        this.ownerType = OwnerType.PLAYER;
    }

    public void renderGUI(SpriteBatch batch, BitmapFont font) {
        sb.setLength(0);
        sb.append("SCORE: ").append(scoreView).append("\n");
        sb.append("HP: ").append(hp).append(" / ").append(hpMax).append("\n");
        sb.append("BULLETS: ").append(currentWeapon.getCurBullets()).append(" / ").append(currentWeapon.getMaxBullets()).append("\n");
        sb.append("MONEY: ").append(money).append("\n");
        sb.append("MAGNET: ").append((int) magneticField.radius);
        font.draw(batch, sb, 20, 700);
    }

    public void update(float dt) {
        super.update(dt);
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
            accelerate(dt);
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
                        1, 1, 1, 1);
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            decelerate(dt);
            float bx = position.x + MathUtils.cosDeg(angle + 90) * 20;
            float by = position.y + MathUtils.sinDeg(angle + 90) * 20;
            backFire(bx, by);
            bx = position.x + MathUtils.cosDeg(angle - 90) * 20;
            by = position.y + MathUtils.sinDeg(angle - 90) * 20;
            backFire(bx, by);

        }

        magneticField.setPosition(position);
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
                    1, 1, 1, 1);
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

    public void consume(PowerUp powerUp) {
        sb.setLength(0);
        switch (powerUp.getType()) {
            case ENERGY:
                int oldHp = hp;
                hp += powerUp.getPower();
                if (hp > hpMax) {
                    hp = hpMax;
                }
                sb.append("HP + ").append(hp - oldHp);
                gc.getInfoController().setup(powerUp.getPosition().x, powerUp.getPosition().y, String.valueOf(sb), Color.GREEN);
                break;
            case MONEY:
                money += powerUp.getPower();
                sb.append("MONEY + ").append(powerUp.getPower());
                gc.getInfoController().setup(powerUp.getPosition().x, powerUp.getPosition().y, String.valueOf(sb), Color.YELLOW);
                break;
            case AMMO:
                currentWeapon.addAmmo(powerUp.getPower());
                sb.append("AMMO + ").append(powerUp.getPower());
                gc.getInfoController().setup(powerUp.getPosition().x, powerUp.getPosition().y, String.valueOf(sb), Color.ORANGE);
                break;
        }
    }

    public boolean upgrade(Skill skill) {
        switch (skill) {
            case HP_MAX:
                hpMax += skill.power;
                return true;
            case HP:
                if (hp + skill.power < hpMax) {
                    hp += skill.power;
                    return true;
                }
                break;
            case WEAPON:
                if (weaponNum < weapons.length - 1) {
                    weaponNum++;
                    currentWeapon = weapons[weaponNum];
                    return true;
                }
            case MAGNET:
                if (magneticField.radius < 500) {
                    magneticField.radius += Skill.MAGNET.power;
                    return true;
                }
        }
        return false;
    }
}
