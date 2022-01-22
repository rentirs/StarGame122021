package com.star.app.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.star.app.game.helpers.ObjectPool;
import com.star.app.screen.utils.Assets;

public class PowerUpController extends ObjectPool<PowerUp> {
    private final TextureRegion[][] textures;

    @Override
    protected PowerUp newObject() {
        return new PowerUp();
    }

    public PowerUpController() {
        this.textures = new TextureRegion(Assets.getInstance().getAtlas().findRegion("powerups"))
                .split(60, 60);
    }

    public void render(SpriteBatch batch) {
        for (PowerUp p : activeList) {
            int frameIndex = (int) (p.getTime() / 0.1f) % textures[p.getType().index].length;
            batch.draw(textures[p.getType().index][frameIndex], p.getPosition().x - 30, p.getPosition().y - 30);
        }
    }

    public void setup(float x, float y, float probability) {
        if (MathUtils.random() <= probability) {
            getActiveElement().activate(PowerUp.Type.values()[MathUtils.random(0, 2)], x, y, 30);
        }
    }

    public void update(float dt) {
        for (PowerUp powerUp : activeList) {
            powerUp.update(dt);
        }
        checkPool();
    }
}
