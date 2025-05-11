package com.app.core.entities;

public class Weapon extends Item{
    private WeaponType weaponType;
    private int power[];
    private int blockValue;
    private int critChance;

    public Weapon(String id, String name, WeaponType weaponType, int[] power, int critChance, int blockValue) {
        super(id, name);
        this.weaponType = weaponType;
        this.power = power;
        this.critChance = critChance;
        this.blockValue = blockValue;
    }

    public int getDamage() {
        return (RANDOM.nextInt(this.power[0], (this.power[1]+1)));
    }

    // Setters and Getters
    public WeaponType getWeaponType() { return this.weaponType; }
    public int[] getPower(){ return this.power; }
    public int getBlockValue() { return this.blockValue; }
    public int getCritChance() { return this.critChance; }
}
