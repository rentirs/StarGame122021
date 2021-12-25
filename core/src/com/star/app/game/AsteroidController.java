package com.star.app.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.star.app.game.helpers.ObjectPool;
import com.star.app.screen.ScreenManager;

public class AsteroidController {
    private Texture asteroidTexture;
    private GameController gc;
    private Asteroid asteroid;

    public AsteroidController(GameController gc) {
        this.gc = gc;
        this.asteroidTexture = new Texture("asteroid.png");
        this.asteroid = new Asteroid();
    }

    public void render(SpriteBatch batch) {
            batch.draw(asteroidTexture, asteroid.getPosition().x - 128, asteroid.getPosition().y - 128);
    }

    public void setup(float x, float y, float vx, float vy) {
    }

    public void update(float dt) {
        asteroid.update(dt);
    }
}
