package com.app.core.entities;

import java.util.Random;

import com.app.core.entities.behaviour.*;
import com.app.core.enums.EntityType;

public abstract class Unit extends BattleEntity implements Movable, Attackable, Attacking{
    protected int currentHealth;
    protected int movementPoints;

    // battle uniques
    protected int direction = 0;

    public Unit(String id, String name) {
        super(id, name, EntityType.UNIT);
        this.id = id;
    }

    // Functions
    

    // Override functions
    @Override
    public boolean isAlive(){
        return this.currentHealth > 0;
    }
    @Override
    public boolean isCriticalHit() {
        int critChance = getCritChance();
        if(critChance <= 0) return false;
        int randomNumber = RANDOM.nextInt(0, 100);
        return (randomNumber < critChance);
    }
    @Override
    public int getCurrentHealth() { return this.currentHealth; }
    @Override
    public int[] takeDamage(int[] damage) {
        if(damage[0] <= 0) return new int[]{0, damage[1]};
        int damageTaken = (damage[0]*damage[1]); // change with armor and stuff;
        this.currentHealth = this.currentHealth - damageTaken;
        if(this.currentHealth < 0) this.currentHealth = 0;
        return new int[]{damageTaken, damage[1]};
    }
    @Override
    public int getMovementPoints() {
        return this.movementPoints;
    }
    @Override
    public void resetMovementPoints() {
        this.movementPoints = speed;
    }
    @Override
    public int consumeMovementPoints(int distanceCovered) {
        if(distanceCovered > this.movementPoints) return (-1);
        this.movementPoints-=distanceCovered;
        return distanceCovered;
    }
    @Override
    public void setDirection(int direction) {
        this.direction = direction;
    }
    @Override
    public int getDirection() { return this.direction; }

    // Abstract functions
    public abstract int[] getAttackDamage(int distance);
    public abstract int getRange();
    public abstract int getCritChance();
    public abstract int getMaxHealth();

    // Setters and Getters
    
}
