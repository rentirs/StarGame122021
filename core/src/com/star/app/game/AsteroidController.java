package com.star.app.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.star.app.game.helpers.ObjectPool;

public class AsteroidController extends ObjectPool<Asteroid> {
    private GameController gameController;

    @Override
    protected Asteroid newObject() {
        return new Asteroid(gameController);
    }

    public AsteroidController(GameController gameController) {
        this.gameController = gameController;
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < activeList.size(); i++) {
            Asteroid asteroid = activeList.get(i);
            asteroid.render(batch);
        }
    }

    public void setup(float x, float y, float vx, float vy, float scale) {
        getActiveElement().activate(x, y, vx, vy, scale);
    }

    public void update(float dt) {
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).update(dt);
        }
        checkPool();
    }
}
