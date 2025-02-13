package com.euhedral.game.Entities.Enemy;

import com.euhedral.engine.Utility;
import com.euhedral.game.EntityHandler;
import com.euhedral.game.GameController;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class EnemyStatic extends Enemy {

    int verticalPosition = 400;
    double destinationX, destinationY;
    double deceleration;

    public EnemyStatic(int x, int y, int levelHeight) {
        super(x, y, levelHeight);
        enemyType = EntityHandler.TYPE_STATIC;
        bulletVelocity = Utility.intAtWidth640(6);

        double decelerationMAX = 0.030;
        double decelerationMIN = 0.005;
        int randMAX = (int) (decelerationMAX / decelerationMIN);
        int decelerationInt = Utility.randomRangeInclusive(1, randMAX);
        deceleration = (double) (decelerationInt) * decelerationMIN;

        attackEffect = true;

        setImage(textureHandler.enemyStatic[0]);
    }

    public EnemyStatic(int x, int y, Color color, int levelHeight) {
        this(x,y, levelHeight);
        this.color = color;
    }

    @Override
    public void initialize() {
        super.initialize();

        shootTimerDefault = 200;
        score = 200;
        velX = 0;
        minVelY = 1.75f;
        distance = 0; // stub ; width * 2;
        movementDistance = distance;
        commonInit();
        damage = 90;
    }

    @Override
    public void update() {
        super.update();
        if (state == STATE_ACTIVE && inscreen) {
            updateDestination();
//            if (movementDistance >= 0) {
//                movementDistance -= Math.abs(velX);
//            } else {
//                velX = 0;
//            }

//            boolean secondsTillShotFire = (shootTimer < 20);
//            if (secondsTillShotFire) {
//
//            }
        }

//        if (state == STATE_EXPLODING) {
////            explosion.runAnimation();
//            if (explosion.playedOnce) {
////                disable();
//            }
//        }
    }

//    @Override
//    public void render(Graphics g) {
//        if (attackEffect) {
//            boolean secondsTillShotFire = (shootTimer < 20);
//            if (isActive() && secondsTillShotFire) {
//                g.setColor(Color.red);
//
//                Graphics2D g2d = (Graphics2D) g;
//                g.setColor(Color.RED);
//
//
//                double drawX = x - (0.5) * (double) width;
//                double drawY = y - (0.5) * (double) height;
//                int arcAngle = 20;
//
//                g2d.setComposite(Utility.makeTransparent(0.5f));
//                g2d.fillArc((int) drawX, (int) drawY, 2 * width, 2 * height, (int) -(getBulletAngle()) - arcAngle / 2, arcAngle);
//                g2d.setComposite(Utility.makeTransparent(1f));
//            }
//        }
//
//        g.setColor(color);
//        super.render(g);
//    }

    @Override
    protected void shoot() {
        super.shoot();
        shootDownDefault();
    }

    @Override
    protected void shootDownDefault() {
        shot += 1;
    }

    @Override
    public void move() {
        super.move();
//        moveHorizontally();
    }

    private void moveShoot() {
//        bullets.add(new BulletEnemy((int) (1.1 * x), y + height / 2, 90));
//        bullets.add(new BulletEnemy(x + (int) (0.8 * width), y + height / 2, 90));
    }

    @Override
    protected void moveInScreen() {
//        if (y < verticalPosition) {
            velY = Math.max(0, velY- deceleration);
            y += velY;
//        }
    }

    public void moveHorizontally() {
        if (movementDistance > 0) {
            movementDistance--;
        } else {
            movementDistance = distance;
        }

        int
                int0 = 0,
                int1 = Utility.perc(distance, 30),
                int2 = Utility.perc(distance, 50),
                int3 = Utility.perc(distance, 80);


        if (movementDistance <= distance && movementDistance > int3) {
            hMove = HorizontalMovement.LEFT;
        } else if (movementDistance <= int3 && movementDistance > int2 || movementDistance <= int1 && movementDistance > int0) {
            hMove = HorizontalMovement.NONE;
        } else if (movementDistance <= int2 && movementDistance > int1) {
            hMove = HorizontalMovement.RIGHT;
        }
//        } else if (movementTimer <= int1 && movementTimer > int0) {
//            hMove = HorizontalMovement.NONE;
//        }
    }

    @Override
    protected void commonInit() {
        this.setHealth(7);
        velY = 2.5f;
    }

    @Override
    public int getTurretX() {
        return (int) x + width/2 - Utility.intAtWidth640(2);
    }

    @Override
    public double getBulletAngle() {
        return calculateAngle(destinationX, destinationY); // stub
    }

    private void updateDestination() {
        destinationX = EntityHandler.playerX;
        destinationY = EntityHandler.playerY;
    }

    @Override
    public void resurrect(int x, int y) {
        commonInit();
        explosion.playedOnce = false;
        super.resurrect(x, y);
    }

    @Override
    protected void renderBounds(Graphics g) {
        g.setColor(Color.green);
        Rectangle2D r = getBounds();

        Graphics2D g2d = (Graphics2D) g;

        g2d.draw(r);
    }

    public boolean checkCollision(Rectangle2D object) {
        Rectangle2D r = getBounds();
        return object.intersects(r);
    }
}
