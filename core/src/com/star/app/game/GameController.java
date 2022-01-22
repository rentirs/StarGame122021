package com.star.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.star.app.screen.ScreenManager;
import com.star.app.screen.utils.Assets;

public class GameController {
    private Background background;
    private BulletController bulletController;
    private AsteroidController asteroidController;
    private Hero hero;
    private Vector2 tempVector;
    private ParticleController particleController;
    private PowerUpController powerUpController;
    private InfoController infoController;
    private BotController botController;
    private Stage stage;
    private boolean pause;
    private int level;
    private float timer;
    private Music music;
    private StringBuilder stringBuilder;

    public BotController getBotController() {
        return botController;
    }

    public InfoController getInfoController() {
        return infoController;
    }

    public float getTimer() {
        return timer;
    }

    public int getLevel() {
        return level;
    }

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
        this.music = Assets.getInstance().getAssetManager().get("audio/mortal.mp3");
        this.music.setLooping(true);
        this.music.play();
        this.level = 1;
        this.background = new Background(this);
        this.hero = new Hero(this);
        this.bulletController = new BulletController(this);
        this.asteroidController = new AsteroidController(this);
        this.tempVector = new Vector2();
        this.particleController = new ParticleController();
        this.powerUpController = new PowerUpController(this);
        this.infoController = new InfoController();
        this.botController = new BotController(this);
        this.stage = new Stage(ScreenManager.getInstance().getViewport(), batch);
        this.stringBuilder = new StringBuilder();
        stage.addActor(hero.getShop());
        Gdx.input.setInputProcessor(stage);
        generateBigAsteroids(1);
        botController.setup(100, 100);
        botController.setup(1000, 100);
    }

    public void generateBigAsteroids(int n) {
        for (int i = 0; i < n; i++) {
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
        timer += dt;
        background.update(dt);
        hero.update(dt);
        bulletController.update(dt);
        asteroidController.update(dt);
        particleController.update(dt);
        powerUpController.update(dt);
        infoController.update(dt);
        botController.update(dt);
        checkCollisions();
        if (!hero.isAlive()) {
            ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAMEOVER, hero);
        }
        if (asteroidController.getActiveList().size() == 0) {
            level++;
            generateBigAsteroids(Math.min(level, 3));
            timer = 0;
        }
        stage.act(dt);
    }

    private void checkCollisions() {
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
                hero.takeDamage(level * 2);
                stringBuilder.setLength(0);
                stringBuilder.append("HP - ").append(level * 2);
                infoController.setup(hero.getPosition().x, hero.getPosition().y, stringBuilder.toString(), Color.RED);
            }
        }

        for (int i = 0; i < asteroidController.getActiveList().size(); i++) {
            Asteroid asteroid = asteroidController.getActiveList().get(i);
            for (int j = 0; j < botController.getActiveList().size(); j++) {
                Bot bot = botController.getActiveList().get(j);
                if(asteroid.getHitArea().overlaps(bot.getHitArea())) {
                    float dst = asteroid.getPosition().dst(bot.getPosition());
                    float halfOverLen = (asteroid.getHitArea().radius + bot.getHitArea().radius - dst) / 2;
                    tempVector.set(bot.getPosition()).sub(asteroid.getPosition()).nor();
                    bot.getPosition().mulAdd(tempVector, halfOverLen);
                    asteroid.getPosition().mulAdd(tempVector, -halfOverLen);
                    float sumScl = bot.getHitArea().radius + asteroid.getHitArea().radius;
                    bot.getVelocity().mulAdd(tempVector, asteroid.getHitArea().radius / sumScl * 100);
                    asteroid.getVelocity().mulAdd(tempVector, -bot.getHitArea().radius / sumScl * 100);
                }
            }
        }

        for (int i = 0; i < bulletController.getActiveList().size(); i++) {
            Bullet bullet = bulletController.getActiveList().get(i);
            for (int j = 0; j < asteroidController.getActiveList().size(); j++) {
                Asteroid asteroid = asteroidController.getActiveList().get(j);
                if (asteroid.getHitArea().contains(bullet.getPosition())) {
                    particleController.getEffectBuilder().bulletCollideWithAsteroid(bullet);
                    bullet.deactivate();
                    if (asteroid.takeDamage(bullet.getOwner().getCurrentWeapon().getDamage())) {
                        if (bullet.getOwner().getOwnerType() == OwnerType.PLAYER) {
                            hero.addScore(asteroid.getHpMax() * 100);
                            for (int k = 0; k < 3; k++) {
                                powerUpController.setup(asteroid.getPosition().x,
                                        asteroid.getPosition().y,
                                        asteroid.getScale() * 0.25f);
                            }
                        }
                    }
                    break;
                }
            }
        }

        for (int i = 0; i < powerUpController.getActiveList().size(); i++) {
            PowerUp powerUp = powerUpController.getActiveList().get(i);
            if (hero.getMagneticField().contains(powerUp.getPosition())) {
                tempVector.set(hero.getPosition()).sub(powerUp.getPosition()).nor();
                powerUp.getVelocity().mulAdd(tempVector, 100);
            }
            if (hero.getHitArea().contains(powerUp.getPosition())) {
                hero.consume(powerUp);
                particleController.getEffectBuilder().takePowerUpEffect(powerUp.getPosition().x,
                        powerUp.getPosition().y,
                        powerUp.getType());
                powerUp.deactivate();
            }
        }

        for (int i = 0; i < bulletController.getActiveList().size(); i++) {
            Bullet bullet = bulletController.getActiveList().get(i);
            if (bullet.getOwner().getOwnerType() == OwnerType.BOT) {
                if (hero.getHitArea().contains(bullet.getPosition())) {
                    hero.takeDamage(bullet.getOwner().getCurrentWeapon().getDamage());
                    bullet.deactivate();
                }
            }
            if ((bullet.getOwner().getOwnerType() == OwnerType.PLAYER)) {
                for (int j = 0; j < botController.getActiveList().size(); j++) {
                    Bot bot = botController.getActiveList().get(j);
                    if (bot.getHitArea().contains(bullet.getPosition())) {
                        bot.takeDamage(bullet.getOwner().getCurrentWeapon().getDamage());
                        bullet.deactivate();
                    }
                }
            }
        }
    }

    public void dispose() {
        background.dispose();
    }
}
