package com.app.core.entities;

import com.app.core.entities.behaviour.Attacking;

public enum WeaponType{
    SWORD(1, 1),
    AXE(1, 1),
    HAMMER(1, 1),
    DAGGER(1, 1),
    SHURIKEN(2, 4),
    BOW(2, 10),
    STAFF(1, 7),
    ORB(1, 6),
    TOTEM(1, 1),
    SHIELD(0, 0),
    SPEAR(1, 2),
    MACE(1, 1);

    private int minRange, maxRange;

    private WeaponType(int minRange, int maxRange) {
        this.minRange = minRange;
        this.maxRange = maxRange;
    }

    public int getMinRange() {
        return this.minRange;
    }
    public int getMaxRange() {
        return this.maxRange;
    }
}
