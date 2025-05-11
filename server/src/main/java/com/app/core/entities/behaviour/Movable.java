package com.app.core.entities.behaviour;

public interface Movable {
    //public abstract setMovementPoints(int movementPoints);
    public abstract int getMovementPoints();
    public abstract void resetMovementPoints();
    public abstract int consumeMovementPoints(int distanceCovered);
    public abstract void setDirection(int direction);
    public abstract int getDirection();
}
