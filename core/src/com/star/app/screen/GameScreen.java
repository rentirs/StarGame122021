package com.star.app.screen;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.star.app.game.GameController;
import com.star.app.game.WorldRendering;
import com.star.app.screen.utils.Assets;

public class GameScreen extends AbstractScreen{
    private GameController gc;
    private WorldRendering worldRendering;

    public GameScreen(SpriteBatch batch) {
        super(batch);
    }

    @Override
    public void show() {
        Assets.getInstance().loadAssets(ScreenManager.ScreenType.GAME);
        this.gc = new GameController();
        this.worldRendering = new WorldRendering(gc, batch);
    }

    @Override
    public void render(float delta) {
        gc.update(delta);
        worldRendering.render();
    }
}
