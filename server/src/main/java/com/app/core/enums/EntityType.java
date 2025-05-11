package com.app.core.enums;

public enum EntityType {
    UNIT(true), 
    BUILDING(false), 
    ITEM(false);

    private boolean movable;
    
    private EntityType(boolean movable) {
        this.movable = movable;
    }

    public boolean getMovable() {
        return this.movable;
    }
}
