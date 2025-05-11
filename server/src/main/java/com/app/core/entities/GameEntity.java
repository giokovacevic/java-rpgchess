package com.app.core.entities;

import java.util.Random;

import com.app.core.enums.EntityType;

public abstract class GameEntity {
    protected String id;
    protected String name;
    protected EntityType entityType;

    protected static final Random RANDOM = new Random();
    
    public GameEntity(String id, String name, EntityType entityType) {
        this.id = id;
        this.name = name;
        this.entityType = entityType;
    }

    public abstract GameEntity copy();

    // Setters and Getters
    public void setId(String id) {
        this.id = id;
    }
    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getId() { return this.id; }
    public EntityType getEntityType() { return this.entityType; }
    public String getName() {
        return this.name;
    }
}
