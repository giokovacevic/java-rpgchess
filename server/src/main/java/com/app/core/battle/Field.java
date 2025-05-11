package com.app.core.battle;

import com.app.core.entities.BattleEntity;

public class Field {
    private final int id;
    private BattleEntity entity = null;
    private boolean blocked  = false;

    // bfs helpers
    private int distance = -1;
    private boolean visited = false;

    public Field(int id) {
        this.id = id;
    }

    // Functions
    public boolean isOccupied() {
        return entity!=null;
    }

    @Override
    public String toString() {
        String str = String.valueOf(this.id);
        if(getBlocked()) {
            str = str + " blocked";
        }else if(isOccupied()) {
            str = str + " occupied " + this.entity.toString();
        }else{
            str = str + " -";
        }
        return str;
    }

    // Setters and Getters
    public void setEntity(BattleEntity entity) {
        this.entity = entity;
        if(this.entity!=null) this.entity.setFieldId(this.id);
    }
    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
    public void setDistance(int distance) {
        this.distance = distance;
    }
    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public int getId() { return this.id; }
    public boolean getBlocked() { return this.blocked; }
    public int getDistance() { return this.distance; }
    public boolean getVisited() { return this.visited; }
    public BattleEntity getEntity() { return this.entity; }
}
