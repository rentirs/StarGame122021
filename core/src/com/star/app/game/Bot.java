package com.star.app.game;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.star.app.game.helpers.Poolable;
import com.star.app.screen.utils.Assets;

public class Bot extends Ship implements Poolable {

    private boolean active;
    private final Vector2 tempVec;

    @Override
    public boolean isActive() {
        return active;
    }

    public Bot(GameController gc) {
        super(gc, 50, 200);
        this.position = new Vector2();
        this.velocity = new Vector2();
        this.tempVec = new Vector2();
        this.texture = Assets.getInstance().getAtlas().findRegion("ship");
        this.hitArea = new Circle(position, 29);
        this.active = false;
        this.ownerType = OwnerType.BOT;
    }

    public void activate(float x, float y) {
        position.set(x, y);
        active = true;
    }

    public void deactivate() {
        active = false;
    }

    public void update(float dt) {
        super.update(dt);

        if (!isAlive()) {
            deactivate();
        }

        tempVec.set(gc.getHero().getPosition()).sub(position).nor();
        angle = tempVec.angleDeg();

        if (gc.getHero().getPosition().dst(position) > 200) {
            accelerate(dt);
            float bx = position.x + MathUtils.cosDeg(angle + 180) * 20;
            float by = position.y + MathUtils.sinDeg(angle + 180) * 20;
            for (int i = 0; i < 3; i++) {
                gc.getParticleController().setup(
                        bx + MathUtils.random(-4, 4),
                        by + MathUtils.random(-4, 4),
                        velocity.x * -0.3f + MathUtils.random(-20, 20),
                        velocity.y * -0.3f + MathUtils.random(-20, 20),
                        0.5f, 1.2f, 0.2f,
                        1, 0, 0, 1,
                        1, 0.5f, 0, 1);
            }
        }
        if (gc.getHero().getPosition().dst(position) < 300) {
            tryToFire();
        }
    }
}
