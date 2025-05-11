package com.app.core.entities;

import com.app.core.enums.EntityType;

public class Item extends GameEntity{

    public Item(String id, String name) {
        super(id, name, EntityType.ITEM);
    }

}
