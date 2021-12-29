package com.star.app.game;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.star.app.screen.ScreenManager;
import com.star.app.screen.utils.Assets;

public class WorldRendering {
    private GameController gc;
    private SpriteBatch batch;
    private BitmapFont font32;
    private StringBuilder sb;
    private StringBuilder lives;

    public WorldRendering(GameController gc, SpriteBatch batch) {
        this.gc = gc;
        this.batch = batch;
        this.font32 = Assets.getInstance().getAssetManager().get("fonts/font32.ttf", BitmapFont.class);
        this.sb = new StringBuilder();
        this.lives = new StringBuilder();
    }

    public void render() {
        ScreenUtils.clear(0.0f, 0.1f, 0.5f, 1);
        batch.begin();
        gc.getBackground().render(batch);
        gc.getAsteroidController().render(batch);
        gc.getHero().render(batch);
        gc.getBulletController().render(batch);
        sb.setLength(0);
        sb.append("SCORE: ").append(gc.getHero().getScoreView());
        font32.draw(batch, sb, 20, 700);
        lives.setLength(0);
        lives.append("LIVES: ").append(gc.getHero().getHp());
        font32.draw(batch, lives, ScreenManager.SCREEN_WIDTH - 130, ScreenManager.SCREEN_HEIGHT - 20);
        batch.end();
    }

}
