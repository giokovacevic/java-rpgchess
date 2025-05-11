package com.app.core.entities;

import java.util.Random;

public class Creature extends Unit{
    protected int maxHealth;
    private int power[];
    private int range;
    private int critChance;
    
    public Creature(String id, String name, int maxHealth, int speed, int[] power, int critChance, int range) {
        super(id, name);
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.speed = speed;
        this.movementPoints = speed;
        this.power = power;
        this.range = range;
        this.critChance = critChance;
    }

    // Override functions
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
