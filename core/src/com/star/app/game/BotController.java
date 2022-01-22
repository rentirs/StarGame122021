package com.star.app.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.star.app.game.helpers.ObjectPool;

public class BotController extends ObjectPool<Bot> {
    private GameController gameController;

    @Override
    protected Bot newObject() {
        return new Bot(gameController);
    }

    public BotController(GameController gameController) {
        this.gameController = gameController;
    }

    public void render(SpriteBatch batch) {
        for (Bot bot : activeList) {
          bot.render(batch);
        }
    }

    public void setup(float x, float y) {
        getActiveElement().activate(x, y);
    }

    public void update(float dt) {
        for (Bot bot : activeList) {
            bot.update(dt);
        }
        checkPool();
    }
}
