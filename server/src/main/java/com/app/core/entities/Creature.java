package com.app.core.entities;

import java.util.Random;

public class Creature extends Unit{
    private int maxHealth;
    private int power[];
    private int range, critChance, accuracy;
    
    public Creature(String id, String name, int maxHealth, int speed, int[] power, int critChance, int range, int accuracy) {
        super(id, name);
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.speed = speed;
        this.movementPoints = speed;
        this.power = power;
        this.range = range;
        this.critChance = critChance;
        this.accuracy = accuracy;
    }

    // Override functions
    @Override
    public String toString() {
        String str = "creature" + " " + getId() + " " + String.valueOf(getControllerId()) + " " + String.valueOf(getDirection()) + " " + String.valueOf(getMaxHealth()) + " " + String.valueOf(getCurrentHealth()); // id alliance direction maxHp currentHp
        return str;
    }

    @Override
    public Creature copy() {
        return new Creature(
            this.getId(),
            this.getName(),
            this.getMaxHealth(),
            this.getSpeed(),
            this.getPower(),
            this.getCritChance(),
            this.getRange(),
            this.getAccuracy()
        );
    }

    @Override
    public int[] getAttackDamage(int distance) {
        if(isCriticalHit()) {
            return (new int[]{RANDOM.nextInt(this.power[0], (this.power[1]+1)), 2});
        }
        return (new int[]{RANDOM.nextInt(this.power[0], (this.power[1]+1)), 1});
    }

    @Override
    public int getRange() { return this.range; }
    
    @Override
    public int getCritChance() {
        return this.critChance;
    }

    @Override
    public int getAccuracy() {
        return this.accuracy;
    }
    
    @Override
    public int getMaxHealth() { return this.maxHealth; }
    
    @Override
    public int[] takeDamage(int[] damage) {
        return super.takeDamage(damage);
    }

    // Setters and Getters
    public void setPower(int[] power) {
        this.power = power;
    }
    public void setRange(int range) {
        this.range = range;
    }

    public int[] getPower() { return this.power; }
}
