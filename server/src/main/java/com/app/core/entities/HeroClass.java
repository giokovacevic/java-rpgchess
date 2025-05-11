package com.app.core.entities;

import java.util.EnumSet;
import java.util.Set;

public enum HeroClass {
    FIGHTER(32, 4, 22, 2, 3, EnumSet.of(WeaponType.SWORD, WeaponType.AXE, WeaponType.MACE), EnumSet.of(WeaponType.SWORD, WeaponType.AXE, WeaponType.MACE)), 
    WARRIOR(34, 5, 20, 1, 2, EnumSet.of(WeaponType.SWORD, WeaponType.AXE, WeaponType.HAMMER), EnumSet.of(WeaponType.SHIELD)), 
    CHAMPION(29, 4, 22, 2, 4, EnumSet.of(WeaponType.SPEAR), EnumSet.of(WeaponType.SHIELD)), 
    ASSASSIN(26, 3, 23, 2, 5, EnumSet.of(WeaponType.DAGGER), EnumSet.of(WeaponType.SHURIKEN)), 
    PALADIN(30, 4, 25, 3, 3, EnumSet.of(WeaponType.SWORD), EnumSet.of(WeaponType.SHIELD)), 
    CLERIC(28, 3, 26, 4, 2, EnumSet.of(WeaponType.STAFF, WeaponType.MACE), EnumSet.of(WeaponType.TOTEM)), 
    CONJURER(24, 2, 30, 4, 1, EnumSet.of(WeaponType.STAFF), EnumSet.of(WeaponType.ORB)), 
    MAGE(25, 2, 32, 4, 1, EnumSet.of(WeaponType.STAFF, WeaponType.ORB), EnumSet.of(WeaponType.ORB)), 
    ARCHER(25, 3, 23, 2, 2, EnumSet.of(WeaponType.BOW), EnumSet.of(WeaponType.DAGGER));

    private int baseHealth, healthPerLevel, baseMana, manaPerLevel, speed;
    private Set<WeaponType> allowedPrimaryWeaponTypes, allowedSecondaryWeaponTypes;

    private HeroClass( int baseHealth, int healthPerLevel, int baseMana, int manaPerLevel, int speed, Set<WeaponType> allowedPrimaryWeaponTypes, Set<WeaponType> allowedSecondaryWeaponTypes) {
        this.baseHealth = baseHealth;
        this.healthPerLevel = healthPerLevel;
        this.baseMana = baseMana;
        this.manaPerLevel = manaPerLevel;
        this.speed = speed;
        this.allowedPrimaryWeaponTypes = allowedPrimaryWeaponTypes;
        this.allowedSecondaryWeaponTypes = allowedSecondaryWeaponTypes;
    }

    public int getBaseHealth() { return this.baseHealth; }
    public int getHealthPerLevel() { return this.healthPerLevel; }
    public int getBaseMana() { return this.baseMana; }
    public int getManaPerLevel() { return this.manaPerLevel; }
    public int getSpeed() { return this.speed; }
    public Set<WeaponType> getAllowedPrimaryWeaponTypes(){ return this.allowedPrimaryWeaponTypes; }
    public Set<WeaponType> getAllowedSecondaryWeaponTypes(){ return this.allowedSecondaryWeaponTypes; }
}
