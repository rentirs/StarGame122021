package com.star.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.star.app.screen.ScreenManager;

public class GameController {
    private Background background;
    private BulletController bulletController;
    private AsteroidController asteroidController;
    private Hero hero;
    private Vector2 tempVector;
    private ParticleController particleController;
    private PowerUpController powerUpController;
    private Stage stage;
    private boolean pause;

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public Stage getStage() {
        return stage;
    }

    public ParticleController getParticleController() {
        return particleController;
    }

    public PowerUpController getPowerUpController() {
        return powerUpController;
    }

    public Hero getHero() {
        return hero;
    }

    public Background getBackground() {
        return background;
    }

    public BulletController getBulletController() {
        return bulletController;
    }

    public AsteroidController getAsteroidController() {
        return asteroidController;
    }

    public boolean isPause() {
        return pause;
    }

    public GameController(SpriteBatch batch) {
        this.background = new Background(this);
        this.hero = new Hero(this);
        this.bulletController = new BulletController(this);
        this.asteroidController = new AsteroidController(this);
        this.tempVector = new Vector2();
        this.particleController = new ParticleController();
        this.powerUpController = new PowerUpController(this);
        this.stage = new Stage(ScreenManager.getInstance().getViewport(), batch);
        stage.addActor(hero.getShop());
        Gdx.input.setInputProcessor(stage);
        for (int i = 0; i < 3; i++) {
            asteroidController.setup(MathUtils.random(0, ScreenManager.SCREEN_WIDTH),
                    MathUtils.random(0, ScreenManager.SCREEN_HEIGHT),
                    MathUtils.random(-200, 200),
                    MathUtils.random(-200, 200),
                    1.0f);
        }
    }

    public void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            setPause(!isPause());
            hero.getShop().setVisible(!hero.getShop().isVisible());
        }
        if (pause) {
            return;
        }
        background.update(dt);
        hero.update(dt);
        bulletController.update(dt);
        asteroidController.update(dt);
        particleController.update(dt);
        powerUpController.update(dt);
        checkCollisions();
        if (!hero.isAlive()) {
            ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAMEOVER, hero);
        }
        if (asteroidController.getActiveList().size() <= 0) {
            ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.MENU);
        }
        stage.act(dt);
    }

    private void checkCollisions() {
        for (int i = 0; i < bulletController.getActiveList().size(); i++) {
            Bullet bullet = bulletController.getActiveList().get(i);
            for (int j = 0; j < asteroidController.getActiveList().size(); j++) {
                Asteroid asteroid = asteroidController.getActiveList().get(j);
                if (asteroid.getHitArea().contains(bullet.getPosition())) {
                    particleController.setup(
                            bullet.getPosition().x + MathUtils.random(-4, 4),
                            bullet.getPosition().y + MathUtils.random(-4, 4),
                            bullet.getVelocity().x * -0.3f + MathUtils.random(-30, 30),
                            bullet.getVelocity().y * -0.3f + MathUtils.random(-30, 30),
                            0.3f, 2.2f, 1.5f,
                            1, 1, 1, 1,
                            0, 0, 1, 0);
                    bullet.deactivate();
                    if (asteroid.takeDamage(hero.getCurrentWeapon().getDamage())) {
                        hero.addScore(asteroid.getHpMax() * 100);
                        for (int k = 0; k < 3; k++) {
                            powerUpController.setup(asteroid.getPosition().x,
                                    asteroid.getPosition().y,
                                    asteroid.getScale() * 0.25f);
                        }
                    }
                    break;
                }
            }
        }

        for (int i = 0; i < asteroidController.getActiveList().size(); i++) {
            Asteroid asteroid = asteroidController.getActiveList().get(i);
            if (asteroid.getHitArea().overlaps(hero.getHitArea())) {
                float dst = asteroid.getPosition().dst(hero.getPosition());
                float halfOverLen = (asteroid.getHitArea().radius + hero.getHitArea().radius - dst) / 2;
                tempVector.set(hero.getPosition()).sub(asteroid.getPosition()).nor();
                hero.getPosition().mulAdd(tempVector, halfOverLen);
                asteroid.getPosition().mulAdd(tempVector, -halfOverLen);
                float sumScl = hero.getHitArea().radius + asteroid.getHitArea().radius;
                hero.getVelocity().mulAdd(tempVector, asteroid.getHitArea().radius / sumScl * 100);
                asteroid.getVelocity().mulAdd(tempVector, -hero.getHitArea().radius / sumScl * 100);

                if (asteroid.takeDamage(2)) {
                    hero.addScore(asteroid.getHpMax() * 50);
                }

                hero.takeDamage(2);
            }
        }

        for (int i = 0; i < powerUpController.getActiveList().size(); i++) {
            PowerUp powerUp = powerUpController.getActiveList().get(i);
            if (hero.getHitArea().overlaps(powerUp.getHitArea())) {
                powerUp.getVelocity().set((hero.getPosition().x - powerUp.getPosition().x) * 2, (hero.getPosition().y - powerUp.getPosition().y) * 2);
            }
            if (hero.getHitArea().contains(powerUp.getPosition())) {
                hero.consume(powerUp);
                particleController.getEffectBuilder().takePowerUpEffect(powerUp.getPosition().x,
                        powerUp.getPosition().y,
                        powerUp.getType());
                powerUp.deactivate();
            }
        }
    }

    public void dispose() {
        background.dispose();
    }
}
