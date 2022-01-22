package com.star.app.game;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.star.app.game.helpers.ObjectPool;
import com.star.app.screen.utils.Assets;

public class ParticleController extends ObjectPool<Particle> {
    public class EffectBuilder {

        public void takePowerUpEffect(float x, float y, PowerUp.Type type) {
            switch (type) {
                case ENERGY:
                    for (int i = 0; i < 16; i++) {
                        float angle = 6.28f / 16 * i;
                        setup(x, y, (float) Math.cos(angle) * 100, (float) Math.sin(angle) * 100,
                                0.8f, 3, 2.5f,
                                0, 1, 0, 1, 0.5f, 1, 0, 0.5f);
                    }
                    break;
                case MONEY:
                    for (int i = 0; i < 16; i++) {
                        float angle = 6.28f / 16 * i;
                        setup(x, y, (float) Math.cos(angle) * 100, (float) Math.sin(angle) * 100,
                                0.8f, 3, 2.5f,
                                1, 1, 0, 1, 1, 0.5f, 0, 0.5f);
                    }
                    break;
                case AMMO:
                    for (int i = 0; i < 16; i++) {
                        float angle = 6.28f / 16 * i;
                        setup(x, y, (float) Math.cos(angle) * 100, (float) Math.sin(angle) * 100,
                                0.8f, 3, 2.5f,
                                1, 0, 0, 1, 1, 0, 1, 0.5f);
                    }
                    break;
            }
        }

        public void bulletCollideWithAsteroid(Bullet bullet) {
            setup(
                    bullet.getPosition().x + MathUtils.random(-4, 4),
                    bullet.getPosition().y + MathUtils.random(-4, 4),
                    bullet.getVelocity().x * -0.3f + MathUtils.random(-30, 30),
                    bullet.getVelocity().y * -0.3f + MathUtils.random(-30, 30),
                    0.3f, 2.2f, 1.5f,
                    1, 1, 1, 1,
                    0, 0, 1, 0);
        }

        public void createBulletTrace(OwnerType ownerType, Bullet bullet) {
            switch (ownerType) {
                case PLAYER:
                    for (int i = 0; i < 2; i++) {
                        setup(
                                bullet.getPosition().x + MathUtils.random(-4, 4),
                                bullet.getPosition().y + MathUtils.random(-4, 4),
                                bullet.getVelocity().x * 0.1f + MathUtils.random(-20, 20),
                                bullet.getVelocity().y * 0.1f + MathUtils.random(-20, 20),
                                0.1f, 1.2f, 0.2f,
                                1, 0.7f, 0, 1,
                                1, 1, 1, 0);
                    }
                    break;
                case BOT:
                    setup(
                            bullet.getPosition().x, bullet.getPosition().y,
                            bullet.getVelocity().x * 0.1f + MathUtils.random(-20, 20),
                            bullet.getVelocity().y * 0.1f + MathUtils.random(-20, 20),
                            0.13f, 2.2f, 1.5f,
                            0, 0.9f, 0, 1,
                            0, 0.8f, 0.1f, 0);
                    break;
            }

        }
    }

    private final TextureRegion oneParticle;
    private final EffectBuilder effectBuilder;

    public EffectBuilder getEffectBuilder() {
        return effectBuilder;
    }

    public ParticleController() {
        this.oneParticle = Assets.getInstance().getAtlas().findRegion("star16");
        this.effectBuilder = new EffectBuilder();
    }

    @Override
    protected Particle newObject() {
        return new Particle();
    }

    public void render(SpriteBatch batch) {
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        for (Particle o : activeList) {
            float t = o.getTime() / o.getTimeMax();
            float scale = lerp(o.getSize1(), o.getSize2(), t);
            colorize(batch, o, t, scale);
        }
        batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        for (Particle o : activeList) {
            float t = o.getTime() / o.getTimeMax();
            float scale = lerp(o.getSize1(), o.getSize2(), t);
            if (MathUtils.random(0, 300) < 3) {
                scale *= 5;
            }
            colorize(batch, o, t, scale);
        }
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void colorize(SpriteBatch batch, Particle o, float t, float scale) {
        batch.setColor(lerp(o.getR1(), o.getR2(), t), lerp(o.getG1(), o.getG2(), t),
                lerp(o.getB1(), o.getB2(), t), lerp(o.getA1(), o.getA2(), t));
        batch.draw(oneParticle, o.getPosition().x - 8, o.getPosition().y - 8,
                8, 8, 16, 16, scale, scale, 0);
    }

    public void setup(float x, float y, float vx, float vy,
                      float timeMax, float size1, float size2,
                      float r1, float g1, float b1, float a1,
                      float r2, float g2, float b2, float a2) {
        Particle item = getActiveElement();
        item.init(x, y, vx, vy, timeMax, size1, size2, r1, g1, b1, a1, r2, g2, b2, a2);
    }

    public void update(float dt) {
        for (Particle particle : activeList) {
            particle.update(dt);
        }
        checkPool();
    }

    public float lerp(float value1, float value2, float point) {
        return value1 + (value2 - value1) * point;
    }
}
