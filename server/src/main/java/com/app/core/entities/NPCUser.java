package com.app.core.entities;

public class NPCUser extends User{
    public NPCUser(String username) {
        super(username);
    }

    @Override
    public boolean isNPC() {
        return true;
    }
}
