package com.app.core.entities.behaviour;

public interface Attackable {
    public abstract int getMaxHealth();
    public abstract int getCurrentHealth();
    public abstract int[] takeDamage(int[] damage);
}
