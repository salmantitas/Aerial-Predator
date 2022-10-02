package com.euhedral.game.Entities.Enemy;

import com.euhedral.engine.Utility;
import com.euhedral.game.ContactID;
import com.euhedral.game.EnemyID;
import com.euhedral.game.Entities.BulletEnemy;

import java.awt.*;

public class EnemyFast extends Enemy{
    public EnemyFast(int x, int y, ContactID contactID) {
        super(x, y, contactID);
        enemyID = EnemyID.Fast;
    }

    public EnemyFast(int x, int y, ContactID contactID, Color color) {
        super(x, y, contactID, color);
        enemyID = EnemyID.Fast;
    }

    @Override
    public void initialize() {
        super.initialize();

        power = 2;
        shootTimerDef = 150;
        velY = 4f;
        healthRange(2,4);
        score = 100;
    }

    @Override
    protected void shoot() {
        super.shoot();
        fastShoot();
    }

    private void fastShoot() {
        int newVel = Utility.intAtWidth640(5);
        double angle = 75;
        bullets.add(new BulletEnemy(x + width/2,y, angle, newVel));
        bullets.add(new BulletEnemy(x + width/2,y, angle + 2 * (90 - angle), newVel));
        if (power == 2) {
            bullets.add(new BulletEnemy(x + width/2,y, 90, newVel));
        }
    }
}