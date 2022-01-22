package com.star.app.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.star.app.game.helpers.ObjectPool;
import com.star.app.screen.utils.Assets;

public class BulletController extends ObjectPool<Bullet> {
    private TextureRegion bulletTexture;
    private GameController gameController;

    @Override
    protected Bullet newObject() {
        return new Bullet(gameController);
    }

    public BulletController(GameController gameController) {
        this.gameController = gameController;
        this.bulletTexture = Assets.getInstance().getAtlas().findRegion("bullet");
    }

    public void render(SpriteBatch batch) {
        for (Bullet b : activeList) {
            batch.draw(bulletTexture, b.getPosition().x - 16, b.getPosition().y - 16);
        }
    }

    public void setup(Ship owner, float x, float y, float vx, float vy) {
        getActiveElement().activate(owner, x, y, vx, vy);
    }

    public void update(float dt) {
        for (Bullet bullet : activeList) {
            bullet.update(dt);
        }
        checkPool();
    }
}
