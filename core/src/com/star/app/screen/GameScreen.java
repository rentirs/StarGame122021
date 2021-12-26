package com.star.app.screen;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.star.app.game.GameController;
import com.star.app.game.WorldRendering;

public class GameScreen extends AbstractScreen{
    private SpriteBatch batch;
    private GameController gc;
    private WorldRendering worldRendering;

    public GameScreen(SpriteBatch batch) {
        this.batch = batch;
    }

    @Override
    public void show() {
        this.gc = new GameController();
        this.worldRendering = new WorldRendering(gc, batch);
    }

    @Override
    public void render(float delta) {
        gc.update(delta);
        worldRendering.render();
    }
}
