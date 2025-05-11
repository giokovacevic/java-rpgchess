package com.app.core.entities;

import com.app.core.enums.EntityType;

public class Item extends GameEntity{
    // protected int value;

    public Item(String id, String name) {
        super(id, name, EntityType.ITEM);
    }

    @Override
    public Item copy(){
        return new Item(
            this.getId(), 
            this.getName()
        );
    }
}
