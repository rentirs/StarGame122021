package com.star.app.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class WorldRendering {
    private GameController gc;
    private SpriteBatch batch;

    public WorldRendering(GameController gc, SpriteBatch batch) {
        this.gc = gc;
        this.batch = batch;
    }

    public void render() {
        ScreenUtils.clear(0.0f, 0.1f, 0.5f, 1);
        batch.begin();
        gc.getBackground().render(batch);
        gc.getHero().render(batch);
        gc.getBulletController().render(batch);
        gc.getAsteroidController().render(batch);
        batch.end();
    }

}
