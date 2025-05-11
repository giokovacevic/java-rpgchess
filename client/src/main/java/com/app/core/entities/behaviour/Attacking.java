package com.app.core.entities.behaviour;

public interface Attacking {
    public abstract int[] getAttackDamage(int distance);
    public abstract int getRange();
    public abstract int getCritChance();
    public abstract boolean isCriticalHit();
}
