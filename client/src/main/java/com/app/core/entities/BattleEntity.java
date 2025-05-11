package com.app.core.entities;

import java.util.Random;

import com.app.core.enums.EntityType;

public abstract class BattleEntity extends GameEntity{
    protected String id;
    protected int speed;

    // battle ids
    protected int controllerId = -1;
    protected int fieldId = -1;
    protected int battleId = -1;
    protected boolean hasCompletedTurn = false;
    
    public BattleEntity(String id, String name, EntityType entityType) {
        super(id, name, entityType);
    }

    // Abstract functions
    public abstract boolean isAlive();

    // Setters and Getters
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    
    public void setControllerId(int controllerId) {
        this.controllerId = controllerId;
    }
    public void setFieldId(int fieldId) {
        this.fieldId = fieldId;
    }
    public void setHasCompletedTurn(boolean hasCompletedTurn) {
        this.hasCompletedTurn = hasCompletedTurn;
    }
    public void setBattleId(int battleId) {
        this.battleId = battleId;
    }
    public void setAllBattleProperties(int controllerId, int battleId, int fieldId) {
        this.controllerId = controllerId;
        this.battleId = battleId;
        this.fieldId = fieldId;
    }

    public int getSpeed() { return this.speed; }
    
    public int getControllerId() { return this.controllerId; }
    public int getBattleId() { return this.battleId; }
    public int getFieldId() { return this.fieldId; }
    public boolean getHasCompletedTurn() { return this.hasCompletedTurn; }
    public String getAlliance(int controllerId) { 
        if(this.controllerId == controllerId) {
            return "ally";
        }
        if((this.controllerId != 0) && (this.controllerId != 1)) return "neutral";
        return "enemy";
    }
}
