package com.euhedral.engine;

import com.euhedral.game.EntityID;

import java.awt.*;

public abstract class MobileEntity extends Entity {

    protected class Physics {
        /**********
         * Physics *
         ***********/

        // this can be completely commented out if the
        // game has no functional use of physics
        protected float gravity = 1f, terminalVel = 0;
        protected float acceleration = 0, frictionalForce = 0;

        // every object is initialized to be not jumping or affected by gravity
        protected boolean gravityAffected = false, jumping = false, friction = false;

        public void enableFriction() {
            friction = true;
        }

        public void setFrictionalForce(float frictionalForce) {
            this.frictionalForce = frictionalForce;
        }

        public void setAcceleration(float acceleration) {
            this.acceleration = acceleration;
        }
    }

    protected Physics physics = new Physics();

    protected enum HorizontalMovement{
        LEFT, RIGHT, NONE;
    }

    protected enum VerticalMovement{
        UP, DOWN, NONE;
    }

    protected double WEST = 0;
    protected double SOUTH_WEST = 45;
    protected double SOUTH = 90;
    protected double SOUTH_EAST = 135;
    protected double EAST = 180;
    protected double NORTH_EAST = 225;
    protected double NORTH = 270;
    protected double NORTH_WEST = 315;

//    protected double turn = SOUTH_WEST - WEST;
    protected float forwardVelocity = 0;
    protected double angle = 0;
    protected double velX, velY;
    protected float minVelX, minVelY;
    protected float maxVelX, maxVelY;
    protected boolean moveLeft, moveRight, moveUp, moveDown;
    protected HorizontalMovement hMove = HorizontalMovement.NONE;
    protected VerticalMovement vMove = VerticalMovement.NONE;

    public MobileEntity(int x, int y, EntityID id) {
        super(x, y, id);
    }

    @Override
    protected void initialize() {

    }

    @Override
    public void update() {
        if (isActive()) {
            move();
        }
    }

//    @Override
//    public void render(Graphics g) {
//        super.render(g);
//    }

    // Add velocity components to coordinates to update screen position
    protected void move() {
        x += velX;
        y += velY;
    }

    // Triangle with vertices A, B, C
    protected void calculateAngle(double bX, double bY) {
        // Coordinates
        double aX = x, aY = y;
        double cX = aX + 1, cY = aY;

        // Vectors
        double vectorABx = aX - bX;
        double vectorABy = (aY - bY);
        double vectorACx = aX - cX;
        double vectorACy = (aY - cY);

        // Magnitudes
        double magnitudeAB = Math.sqrt(Math.pow(vectorABx,2) + Math.pow(vectorABy, 2));
        double magnitudeAC = Math.sqrt(Math.pow(vectorACx,2) + Math.pow(vectorACy, 2));

        // Dot Product
        double dotProduct = vectorABx * vectorACx + vectorABy * vectorACy;

        // Final
        double fraction = dotProduct/(magnitudeAB*magnitudeAC);
        angle = Math.toDegrees(Math.acos(fraction));

        if (bY < aY) {
            angle = - angle;
        }
    }

    // Calculate the velX and velY using angle (of direction) and forward velocity
    protected void calculateVelocities() {
        double angleX;
        double angleY;

        angleX = Math.toRadians(360 - angle);
        angleY = Math.toRadians(angle);

        velX = (float) (forwardVelocity * Math.cos(angleX));
        velY = (float) (forwardVelocity * Math.sin(angleY));
    }

    // Calculate the x and y components based on the forward velocity and normalized vector,
    // using (x,y) as the base and (destinationX, destinationY) as the target angle
    protected void calculateVelocities(double destinationX, double destinationY) {
        // Vector
        double vectorABx = destinationX - x;
        double vectorABy = destinationY - y;

        // Magnitude
        double magnitudeAB = Math.sqrt(Math.pow(vectorABx,2) + Math.pow(vectorABy, 2));

        // Normal Vector
        double normX = vectorABx/magnitudeAB;
        double normY = vectorABy/magnitudeAB;

        velX = forwardVelocity * normX;
        velY = forwardVelocity * normY;
    }

    public double getVelX() {
        return velX;
    }

    public double getVelY() {
        return velY;
    }

    /*
    * Physics Function
    * */

    public boolean isGravityAffected() {
        return physics.gravityAffected;
    }

    public void setGravityAffected(boolean gravityAffected) {
        physics.gravityAffected = gravityAffected;
    }

    public boolean isJumping() {
        return physics.jumping;
    }

    public void setJumping(boolean jumping) {
        physics.jumping = jumping;
    }

    public void setForwardVelocity(float forwardVelocity) {
        this.forwardVelocity = forwardVelocity;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    protected double getCenterX() {
        return (x + width / 2 - 2);
    }
}
