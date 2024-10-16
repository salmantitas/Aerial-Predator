package com.euhedral.game.Entities.Enemy;

import com.euhedral.engine.*;
import com.euhedral.game.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Random;

/*
 *  Standard Enemies, flies downwards and shoots a missile at intervals
 * */
public class Enemy extends MobileEntity {

    protected int health;
    protected int power = 1;
    protected ContactID contactId;
    protected EnemyID enemyID;
    protected double offscreenVelY;
    protected boolean moveLeft, moveRight;
    protected Color color;
    protected int shootTimerDefault = 150;
    protected int shootTimer = shootTimerDefault;
    protected boolean inscreen = false;
    protected float cam;
    protected Random r;
    protected int score = 50;
    protected int distance;
    protected int shotNum = 0;
    protected int movementDistance;

    protected Animation explosion;

    protected int levelHeight;
//    protected boolean alive = true;

    protected boolean shot = false;

    // State Machine
    protected final int STATE_EXPLODING = 2;

    public Enemy(int x, int y, ContactID contactID, int levelHeight) {
        super(x, y, EntityID.Enemy);
        this.enemyID = EnemyID.Basic;
        contactId = ContactID.Air;
        offscreenVelY = velY;
        moveRight = false;
        moveLeft = false;
        cam = GameController.getCamera().getMarker();
        power = 1;
        width = Utility.intAtWidth640(32);
        height = width;
        color = Color.red;
        this.contactId = contactID;
        r = new Random();
        setLevelHeight(levelHeight);
        initialize();
    }

    public Enemy(int x, int y, ContactID contactID, Color color, int levelHeight) {
        this(x, y, contactID, levelHeight);
        this.color = color;
    }

    @Override
    public void update() {
//        updateActive();
        if (state == STATE_ACTIVE) {
            super.update();
            shootTimer--;
            if (!inscreen) {
                inscreen = y > cam + Utility.percHeight(30);
            }
            if (inscreen && isActive()) {
                if (shootTimer <= 0) {
                    shoot();
                }
            }
        } else if (state == STATE_EXPLODING) {
//            super.update();
            move();
//            Utility.log("Exploding");
        }
    }

    protected void updateActive() {

    }

    protected void updateExploding() {

    }

    @Override
    public void move() {
        x = Utility.clamp(x, 0, Engine.WIDTH - Utility.intAtWidth640(width));

        if (isActive()) {
            if (inscreen) {
                moveInScreen();
            } else {
                y += offscreenVelY;
            }
        } else if(isExploding()) {
            velY = 1.5f;
            y += velY;
        }
    }

    @Override
    public void initialize() {
        if (contactId == ContactID.Ground) {
            width = Utility.intAtWidth640(32);
            height = 2* width;
            color = Color.pink;
            r = new Random();
            health = r.nextInt(3) + 2;
            minVelY = 2f;
            velY = minVelY;
        }
        explosion = new Animation(10, GameController.getTexture().explosion[0],
                GameController.getTexture().explosion[1],
                GameController.getTexture().explosion[2],
                GameController.getTexture().explosion[3],
                GameController.getTexture().explosion[4]
        );
    }

    @Override
    public void render(Graphics g) {
        super.render(g);
    }

    public void renderShadow(Graphics g) {
        if (state != STATE_INACTIVE) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setComposite(Utility.makeTransparent(0.5f));

            int offsetX = (int) (Engine.WIDTH / 2 - getCenterX()) / 15;
            int sizeOffset = 10;
            int offsetY = 10 + (int) y / 500;
            g.setColor(Color.DARK_GRAY);
            g.fillRect((int) x - offsetX, (int) y + offsetY, width - sizeOffset, height - sizeOffset);

            g2d.setComposite(Utility.makeTransparent(1f));
        }
    }

    protected void shoot() {
        shotNum++;
        resetShooter();
    }

    protected void shootDownDefault() {
        shot = true;
    }

    public void moveInScreen() {
        y += velY;
        x += velX;
    }

    public void damage() {
        this.health--;
    }

    public int getHealth() {
        return health;
    }

    protected void setHealth(int health) {
        this.health = health;
    }

    protected void setHealth(int min, int max) {
        health = Utility.randomRange(min, max);
    }

    public boolean isInscreen() {
        return inscreen;
    }

    public void setInscreen(boolean inscreen) {
        this.inscreen = inscreen;
    }

    protected void resetShooter() {
        shootTimer = shootTimerDefault;
    }

    public ContactID getContactId() {
        return contactId;
    }

    public int getScore() {
        return score;
    }

    public void setHMove(String move) {
        if (move == "left") {
            hMove = HorizontalMovement.LEFT;
            velX = -minVelX;
        } else if (move == "right") {
            hMove = HorizontalMovement.RIGHT;
            velX = minVelX;//*1.45f;//1.95f;
        } else {
            velX = 0;
        }
    }

    public void setLevelHeight(int levelHeight) {
        this.levelHeight = levelHeight;
    }

    public void destroy() {
        state = STATE_EXPLODING;
        velX = 0;
        SoundHandler.playSound(SoundHandler.EXPLOSION);
    }

//    public boolean isAlive() {
//        return state == STATE_ACTIVE;
//    }

    public Rectangle2D getBoundsHorizontal() {
        Rectangle2D bounds = new Rectangle2D.Double(x, y, width, 1*height/3 + 2);
//        Rectangle bounds = new Rectangle(x, y, width, 1*height/3 + 2);
        return bounds;
    }

    public Rectangle2D getBoundsVertical() {
        Rectangle2D bounds = new Rectangle2D.Double(x + (width / 4), y, (2 * width) / 4, height);
//        Rectangle bounds = new Rectangle(x + (width / 4), y, (2 * width) / 4, height);
        return bounds;
    }

    @Override
    protected void renderBounds(Graphics g) {
        g.setColor(Color.green);
        Rectangle2D r1 = getBoundsVertical();
        Rectangle2D r2 = getBoundsHorizontal();

        Graphics2D g2d = (Graphics2D) g;

        g2d.draw(r1);
        g2d.draw(r2);

//        g2d.drawRect(r1.x, r1.y, r1.width, r1.height);
//        g2d.drawRect(r2.x, r2.y, r2.width, r2.height);

    }

    @Override
    public void resurrect(int x, int y) {
        super.resurrect(x, y);
        shootTimer = shootTimerDefault;
        inscreen = false;
    }

    public boolean checkDeathAnimationEnd() {
        return explosion.playedOnce;
    }

    public void setMovementDistance(int time) {
        this.movementDistance = time;
    }

    @Override
    public void disable() {
        super.disable();
    }

    @Override
    public void clear() {
        super.clear();
        explosion.endAnimation();
//        explosion.playedOnce = false;
    }

    public boolean hasShot() {
        return shot;
    }

    public void resetShot() {
        shot = false;
    }

    //stub
    public int getTurretX() {
        return 0;
    }

    public boolean isExploding() {
        return state == STATE_EXPLODING;
    }

    // Private Methods
}
