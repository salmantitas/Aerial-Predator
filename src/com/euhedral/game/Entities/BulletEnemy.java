package com.euhedral.game.Entities;

import com.euhedral.game.SoundHandler;

import java.awt.*;

public class BulletEnemy extends Bullet{

    BulletEnemy(int x, int y) {
        super(x, y);
    }

    public BulletEnemy(int x, int y, double angle) {
        super(x, y, angle);
        SoundHandler.playSound(SoundHandler.BULLET_ENEMY);
    }

    public BulletEnemy(int x, int y, double angle, int vel) {
        super(x, y, angle);
        this.vel = vel;
    }

    @Override
    protected void drawDefault(Graphics g) {
        g.setColor(Color.orange);
        g.fillOval(x, y, width, height);
    }

    @Override
    public void resurrect(int x, int y) {
        super.resurrect(x, y);
        SoundHandler.playSound(SoundHandler.BULLET_ENEMY);
    }
}
